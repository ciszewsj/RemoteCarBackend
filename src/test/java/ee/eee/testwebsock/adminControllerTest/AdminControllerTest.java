package ee.eee.testwebsock.adminControllerTest;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.properties.ApplicationProperties;
import ee.eee.testwebsock.utils.CustomAuthenticationObject;
import ee.eee.testwebsock.webcontroller.adminconfig.WebAdminCarController;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@Slf4j
public class AdminControllerTest {

	CarControllerUseCase carControllerUseCase;
	CarImplService carImplService;
	CarEntityRepository carEntityRepository;
	ApplicationProperties properties;
	CustomAuthenticationObject authenticationObject;

	WebAdminCarController carController;

	@BeforeEach
	public void beforeTest() {
		carControllerUseCase = Mockito.mock(CarControllerUseCase.class);
		carImplService = Mockito.mock(CarImplService.class);
		carEntityRepository = Mockito.mock(CarEntityRepository.class);
		properties = Mockito.mock(ApplicationProperties.class);
		authenticationObject = Mockito.mock(CustomAuthenticationObject.class);
		Mockito.when(authenticationObject.getId()).thenReturn("userId");

		carController = new WebAdminCarController(carEntityRepository, carControllerUseCase, carImplService, properties);
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

		CarEntity response = carController.getAdminCar(id);

		Assertions.assertEquals(response.getId(), id);
	}

	@Test
	public void startCar() {
		Long id = 0L;
		carController.startCar(id);
	}

	@Test
	public void stopCar() {
		Long id = 0L;
		Mockito.when(carControllerUseCase.isCarRunning(anyLong())).thenReturn(true);

		carController.stopCar(id);
	}

	@Test
	public void forceStopCar() {
		Long id = 0L;
		Mockito.when(carControllerUseCase.isCarRunning(anyLong())).thenReturn(true);

		carController.forceStopCar(id);
	}
}
