package ee.eee.testwebsock.webcontroller.adminconfig;

import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.webcontroller.adminconfig.requests.AddCarRequest;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/car_admin")
@RequiredArgsConstructor
public class WebAdminCarController {

	private final CarEntityRepository carRepository;
	private final CarControllerUseCase carController;

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
	public void addCar(@RequestBody AddCarRequest addCarRequest) {
		CarEntity car = new CarEntity();
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car.setFps(addCarRequest.getFps());
		car.setStatus(CarEntity.ConnectionStatus.DISCONNECTED);
		car = carRepository.save(car);

		carController.addNewCar(car);
	}

	@Transactional
	@PutMapping("/{id}")
	public void updateCar(@PathVariable Long id, @RequestBody AddCarRequest addCarRequest) {
		CarEntity car = carRepository.findById(id)
				.orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car.setFps(addCarRequest.getFps());
		car = carRepository.save(car);

		carController.configCar(car);
	}

	@Transactional
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
