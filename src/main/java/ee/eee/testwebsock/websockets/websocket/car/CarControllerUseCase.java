package ee.eee.testwebsock.websockets.websocket.car;

import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.websockets.data.ControlMessage;

import java.io.IOException;

public interface CarControllerUseCase {

	void addNewCar(CarEntity car);

	void connectCar(Long id);

	void configCar(CarEntity car);

	void releaseCar(Long id);

	void controlCar(Long id, ControlMessage controlMessage) throws IOException;

	void deleteCar(Long id);

	boolean isCarRunning(Long id);

	boolean isCarExists(Long id);
}
