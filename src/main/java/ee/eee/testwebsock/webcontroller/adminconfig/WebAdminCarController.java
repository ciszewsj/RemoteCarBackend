package ee.eee.testwebsock.webcontroller.adminconfig;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.properties.ApplicationProperties;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.webcontroller.adminconfig.requests.AddCarRequest;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ee.eee.testwebsock.utils.WebControllerException.ExceptionStatus.COULD_NOT_FIND_FILE;
import static ee.eee.testwebsock.utils.WebControllerException.ExceptionStatus.COULD_NOT_SAVE_FILE;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/car_admin")
@RequiredArgsConstructor
public class WebAdminCarController {

	private final CarEntityRepository carRepository;
	private final CarControllerUseCase carController;
	private final CarImplService carImplService;
	private final ApplicationProperties properties;

	@GetMapping
	public List<CarEntity> getAdminCars() {
		return carRepository.findAll();
	}

	@GetMapping("/{id}")
	public CarEntity getAdminCar(@PathVariable Long id) {
		return carRepository.findById(id)
				.orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));
	}

	@PostMapping
	public Long addCar(@Validated @RequestBody AddCarRequest addCarRequest) {
		CarEntity car = new CarEntity();
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car = carRepository.save(car);

		carController.addNewCar(car);
		carImplService.addCarStatus(car.getId(), CarStatusEntity.Status.CREATED);
		return car.getId();
	}

	@PutMapping("/{id}")
	public Long updateCar(@PathVariable Long id, @Validated @RequestBody AddCarRequest addCarRequest) {
		CarEntity car = carRepository.findById(id)
				.orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());

		car = carRepository.save(car);
		carController.configCar(car);
		return car.getId();
	}

	@DeleteMapping("/{id}")
	public void deleteCar(@PathVariable Long id) {
		CarEntity car = carRepository.findById(id)
				.orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));

		if (carController.isCarRunning(id)) {
			carController.releaseCar(id);
		}
		carController.deleteCar(id);
		carRepository.delete(car);
	}

	@PostMapping("/start/{id}")
	public void startCar(@PathVariable Long id) {
		if (carController.isCarRunning(id)) {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_RUNNING);
		}
		carController.connectCar(id);
	}

	@PostMapping("/stop/{id}")
	public void stopCar(@PathVariable Long id) {
		if (!carController.isCarRunning(id)) {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_NOT_RUNNING);
		}
		carController.releaseCar(id);
	}

	@PostMapping("/image/{id}")
	public void addImage(@PathVariable Long id,
	                     @RequestParam("photo") MultipartFile file) {
		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get(properties.getPathToSave() + "/" + id);
			Files.write(path, bytes);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebControllerException(COULD_NOT_SAVE_FILE);
		}

	}

	@DeleteMapping("/image/{id}")
	public void deleteImage(@PathVariable Long id) {
		try {
			Path path = Paths.get(properties.getPathToSave() + "/" + id);
			if (Files.exists(path)) {
				Files.delete(path);
			} else {
				throw new WebControllerException(COULD_NOT_FIND_FILE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebControllerException(COULD_NOT_FIND_FILE);
		}
	}
}
