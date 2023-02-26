package ee.eee.testwebsock.websockets.websocket;

import ee.eee.testwebsock.websockets.data.ControlMessage;

import java.io.IOException;

public interface CarControllerUseCase {

	void addNewCar();

	void configNewCar();

	void releaseCar();

	void controlCar(ControlMessage controlMessage) throws IOException;
}
