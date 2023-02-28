package ee.eee.testwebsock.websockets.websocket.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.car.CarConfigMessage;
import ee.eee.testwebsock.websockets.data.car.CarControlMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	}


	@Override
	public void addNewCar(CarEntity car) {
		CarClient carClient = new CarClient(car.getUrl(), car.getFps(), userControllerUseCase);
		carClientMap.put(car.getId(), carClient);
	}

	@Override
	public void connectCar(Long id) {
		if (!carClientMap.get(id).isConnected()) {
			carClientMap.get(id).connect();
		} else {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_RUNNING);
		}
	}

	@Override
	public void releaseCar(Long id) {
		carClientMap.get(id).disconnect();
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
	public void controlCar(Long id, ControlMessage controlMessage) {
		CarClient car = carClientMap.get(id);
		if (car != null) {
			if (car.isConnected()) {
				try {
					carClientMap.get(id).sendCommand("commandSend");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			throw new IllegalStateException("Car not connected");
		}
		throw new IllegalStateException("Car not exists");
	}

	@Override
	public void deleteCar(Long id) {
		CarClient car = getCarWSById(id);
		if (car.isConnected()) {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_RUNNING);
		}
		carClientMap.remove(id);
	}

	@Override
	public boolean isCarRunning(Long id) {
		return getCarWSById(id).isConnected();
	}


	private CarClient getCarWSById(Long id) {
		if (carClientMap.containsKey(id)) {
			return carClientMap.get(id);
		}
		throw new IllegalArgumentException("Could not find car with id: " + id);
	}

}
