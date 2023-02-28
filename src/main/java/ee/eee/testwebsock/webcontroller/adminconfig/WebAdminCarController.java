package ee.eee.testwebsock.webcontroller.adminconfig;

import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarEntityRepository;
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

	@GetMapping
	public List<CarEntity> getAdminCars() {
		return carRepository.findAll();
	}

	@GetMapping("/{id}")
	public CarEntity getAdminCar(@PathVariable Long id) {
		return carRepository.findById(id).orElse(null);
	}

	@PostMapping
	public void addCar(@RequestBody AddCarRequest addCarRequest) {
		CarEntity car = new CarEntity();
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car.setFps(addCarRequest.getFps());
		car = carRepository.save(car);

		carController.addNewCar(car);
	}

	@PutMapping("/{id}")
	public void updateCar(@PathVariable Long id, @RequestBody AddCarRequest addCarRequest) {
		CarEntity car = carRepository.findById(id).orElse(null);
		car.setName(addCarRequest.getName());
		car.setUrl(addCarRequest.getUrl());
		car.setFps(addCarRequest.getFps());
		car = carRepository.save(car);

		carController.configCar(car);
	}

	@DeleteMapping("/{id}")
	public void deleteCar(@PathVariable Long id) {
		if (carController.isCarRunning(id)) {
			carController.releaseCar(id);
		}
		carRepository.delete(null);
		carController.deleteCar(id);
	}

	@PostMapping("/start/{id}")
	public void startCar(@PathVariable Long id) {
		carController.connectCar(id);
	}

	@PostMapping("/stop/{id}")
	public void stopCar(@PathVariable Long id) {
		carController.releaseCar(id);
	}
}
