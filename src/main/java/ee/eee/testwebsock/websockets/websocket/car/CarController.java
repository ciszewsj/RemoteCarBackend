package ee.eee.testwebsock.websockets.websocket.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.car.CarConfigMessage;
import ee.eee.testwebsock.websockets.data.car.CarControlMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class CarController implements CarControllerUseCase {

	private final Map<Long, CarClient> carClientMap;
	private final UserControllerUseCase userControllerUseCase;

	private final ObjectMapper objectMapper;

	public CarController(UserControllerUseCase userControllerUseCase) {
		carClientMap = new HashMap<>();
		this.objectMapper = new ObjectMapper();
		this.userControllerUseCase = userControllerUseCase;
//		addNewCar();
	}


	@Override
	public void addNewCar(CarEntity car) {
		CarClient carClient = new CarClient(car.getUrl(), car.getFps(), userControllerUseCase);
		carClientMap.put(car.getId(), carClient);
	}

	@Override
	public void connectCar(Long id) {
		try {
			carClientMap.get(0L).connect();
//			controlCar(new ControlMessage());
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configCar(CarEntity car) {
		CarControlMessage<CarConfigMessage> carControlMessage =
				new CarControlMessage<>(
						CarControlMessage.CarControlMessageType.CONFIG_MESSAGE,
						CarConfigMessage.builder()
								.fps(car.getFps())
								.build()
				);
	}

	@Override
	public void releaseCar(Long id) {
	}

	@Override
	public void controlCar(Long id, ControlMessage controlMessage) {
		log.error(controlMessage.toString());
//		this.lastControlMessage = controlMessage;
	}

	@Override
	public void deleteCar(Long id) {

	}

	@Override
	public boolean isCarRunning(Long id) {
		return false;
	}


	private CarClient getCarWSById(Long id) {
		if (carClientMap.containsKey(id)) {
			return carClientMap.get(id);
		}
		throw new IllegalArgumentException("Could not find car with id: " + id);
	}

}
