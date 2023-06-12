package ee.eee.testwebsock.userControllerTest;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.properties.ApplicationProperties;
import ee.eee.testwebsock.utils.CustomAuthenticationObject;
import ee.eee.testwebsock.webcontroller.cars.WebCarController;
import ee.eee.testwebsock.webcontroller.cars.requests.CarRentRequest;
import ee.eee.testwebsock.webcontroller.cars.responses.CarRepresentationResponse;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Slf4j
public class UserControllerTest {

	CarControllerUseCase carControllerUseCase;
	CarImplService carImplService;
	CarEntityRepository carEntityRepository;
	ApplicationProperties properties;
	CustomAuthenticationObject authenticationObject;

	WebCarController carController;

	@BeforeEach
	public void beforeTest() {
		carControllerUseCase = Mockito.mock(CarControllerUseCase.class);
		carImplService = Mockito.mock(CarImplService.class);
		carEntityRepository = Mockito.mock(CarEntityRepository.class);
		properties = Mockito.mock(ApplicationProperties.class);
		authenticationObject = Mockito.mock(CustomAuthenticationObject.class);
		Mockito.when(authenticationObject.getId()).thenReturn("userId");

		carController = new WebCarController(carControllerUseCase, carImplService, carEntityRepository, properties);
	}


	@Test
	public void getCar() {
		CarEntity car = Mockito.mock(CarEntity.class);
		Long id = 0L;
		String name = "name";
		String url = "url";
		CarEntity.ConnectionStatus status = CarEntity.ConnectionStatus.CONNECTED;
		Mockito.when(car.getId()).thenReturn(id);
		Mockito.when(car.getStatus()).thenReturn(status);
		Mockito.when(car.getName()).thenReturn(name);
		Mockito.when(car.getUrl()).thenReturn(url);
		Mockito.when(carEntityRepository.findById(anyLong())).thenReturn(Optional.of(car));
		Mockito.when(carControllerUseCase.isCarFree(anyLong())).thenReturn(true);
		Mockito.when(carControllerUseCase.isCarRunning(anyLong())).thenReturn(true);
		Mockito.when(carControllerUseCase.leftControlTime(anyLong())).thenReturn(0L);

		CarRepresentationResponse response = carController.getCar(id);

		Assertions.assertEquals(response.getId(), id);
		Assertions.assertEquals(response.getCarName(), name);
		Assertions.assertEquals(response.getLeftRentedTime(), 0L);
		Assertions.assertEquals(response.getIsCarRunning(), true);
		Assertions.assertEquals(response.getIsCarFree(), true);
	}

	@Test
	public void rentCar() {
		Long id = 0L;

		carController.rentCar(id, authenticationObject);

		Mockito.verify(carImplService, Mockito.times(1)).addCarStatus(Mockito.anyLong(), Mockito.any());
	}

	@Test
	public void takeControl() {
		Long id = 0L;
		CarRentRequest request = new CarRentRequest();

		carController.takeControl(id, request, authenticationObject);
	}
}
