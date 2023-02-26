package ee.eee.testwebsock.websockets.websocket.car;

import ee.eee.testwebsock.websockets.data.ControlMessage;

import java.io.IOException;

public interface CarControllerUseCase {

	void addNewCar();

	void configCar() throws IOException;

	void releaseCar();

	void controlCar(ControlMessage controlMessage);
}
