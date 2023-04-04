package ee.eee.testwebsock.webcontroller.cars;

import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.webcontroller.cars.responses.CarConfigResponse;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class WebCarController {

	private final CarControllerUseCase carControllerUseCase;
	private final CarImplService carImplService;

	@GetMapping
	public void getCars() {

	}

	@GetMapping("/{id}")
	public void getCar(@PathVariable Long id) {

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
}
