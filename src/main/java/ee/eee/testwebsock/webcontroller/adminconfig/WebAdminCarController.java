package ee.eee.testwebsock.webcontroller.adminconfig;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.webcontroller.adminconfig.requests.AddCarRequest;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/car_admin")
@RequiredArgsConstructor
public class WebAdminCarController {

	private final CarEntityRepository carRepository;
	private final CarControllerUseCase carController;
	private final CarImplService carImplService;

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
	public Long addCar(@RequestBody AddCarRequest addCarRequest) {
		CarEntity car = new CarEntity();
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car.setFps(addCarRequest.getFps());
		car = carRepository.save(car);

		carController.addNewCar(car);
		carImplService.addCarStatus(car.getId(), CarStatusEntity.Status.CREATED);
		return car.getId();
	}

	@PutMapping("/{id}")
	public Long updateCar(@PathVariable Long id, @RequestBody AddCarRequest addCarRequest) {
		CarEntity car = carRepository.findById(id)
				.orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car.setFps(addCarRequest.getFps());
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
}
