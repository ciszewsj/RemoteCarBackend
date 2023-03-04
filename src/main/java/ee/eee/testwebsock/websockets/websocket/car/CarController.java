package ee.eee.testwebsock.websockets.websocket.car;

import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.utils.WebControllerException;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CarController implements CarControllerUseCase {

	private final Map<Long, CarClient> carClientMap = new HashMap<>();
	private final UserControllerUseCase userControllerUseCase;
	private final CarImplService carImplService;

	@Override
	public void addNewCar(CarEntity car) {
		CarClient carClient = new CarClient(car.getId(), car.getUrl(), car.getFps(), userControllerUseCase, carImplService);
		carClientMap.put(car.getId(), carClient);
	}

	@Override
	public void connectCar(Long id) {
		CarClient car = getCarWSById(id);
		if (!car.isConnected()) {
			car.connect();
		} else {
			throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_RUNNING);
		}
	}

	@Override
	public void releaseCar(Long id) {
		getCarWSById(id).disconnect();
	}

	@Override
	public void configCar(CarEntity car) {
		getCarWSById(car.getId()).configure(car.getUrl(), car.getFps());
	}


	@Override
	public void controlCar(Long id, ControlMessage controlMessage) {
		CarClient car = getCarWSById(id);
		if (car.isConnected()) {
			car.controlCar(controlMessage);
		}
		throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_IS_NOT_RUNNING);
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
		throw new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND);
	}

	@Override
	public boolean isCarExists(Long id) {
		return carClientMap.containsKey(id);
	}

}
