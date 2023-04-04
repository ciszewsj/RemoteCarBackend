package ee.eee.testwebsock.webcontroller.cars;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.webcontroller.cars.responses.CarConfigResponse;
import ee.eee.testwebsock.webcontroller.cars.responses.CarRepresentationResponse;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class WebCarController {

	private final CarControllerUseCase carControllerUseCase;
	private final CarImplService carImplService;
	private final CarEntityRepository carEntityRepository;

	@GetMapping
	public List<CarRepresentationResponse> getCars() {
		return carEntityRepository.findAll().stream()
				.map(this::mapCarToRepresentation).collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public CarRepresentationResponse getCar(@PathVariable Long id) {
		CarEntity car = carEntityRepository.findById(id).orElseThrow();
		return mapCarToRepresentation(car);
	}

	@PostMapping("/rent/{id}")
	public void rentCar(@PathVariable Long id) {
		carControllerUseCase.rentACar(id, null);
		carImplService.addCarStatus(id, CarStatusEntity.Status.RENT);
	}

	@GetMapping("/config")
	public CarConfigResponse getInfo() {
		return new CarConfigResponse();
	}

	private CarRepresentationResponse mapCarToRepresentation(CarEntity car) {
		return CarRepresentationResponse.builder()
				.id(car.getId())
				.carName(car.getName())
				.isCarFree(carControllerUseCase.isCarFree(car.getId()))
				.isCarRunning(carControllerUseCase.isCarRunning(car.getId()))
				.leftRentedTime(carControllerUseCase.leftControlTime(car.getId()))
				.build();
	}
}
