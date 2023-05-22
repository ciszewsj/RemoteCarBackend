package ee.eee.testwebsock.webcontroller.cars;

import ee.eee.testwebsock.utils.CustomAuthenticationObject;
import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.webcontroller.cars.requests.CarRentRequest;
import ee.eee.testwebsock.webcontroller.cars.responses.CarConfigResponse;
import ee.eee.testwebsock.webcontroller.cars.responses.CarRepresentationResponse;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static ee.eee.testwebsock.utils.WebControllerException.ExceptionStatus.COULD_NOT_FIND_FILE;

@Slf4j
@RestController
@RequestMapping("/car")
@CrossOrigin
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
	public void rentCar(@PathVariable Long id,
	                    @AuthenticationPrincipal CustomAuthenticationObject user) {
		log.info("carId {} try rented by {}", id, user.getId());
		carControllerUseCase.rentACar(id, user.getId());
		carImplService.addCarStatus(id, CarStatusEntity.Status.RENT);
	}

	@PostMapping("/take_control/{id}")
	public void takeControl(@PathVariable Long id,
	                        @Validated @RequestBody CarRentRequest request,
	                        @AuthenticationPrincipal CustomAuthenticationObject user) {
		log.info("carId {} try steering by {} with webSockId {}", id, user.getId(), request.getWebsocketId());

		carControllerUseCase.takeSteering(id, request.getWebsocketId(), user.getId());
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

	@GetMapping("/image/{id}")
	public Resource getImage(@PathVariable Long id) {
		try {
			Path path = Paths.get("ścieżka/do/zapisu/" + id);
			Resource resource = new UrlResource(path.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new WebControllerException(COULD_NOT_FIND_FILE);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WebControllerException(COULD_NOT_FIND_FILE);
		}
	}
}
