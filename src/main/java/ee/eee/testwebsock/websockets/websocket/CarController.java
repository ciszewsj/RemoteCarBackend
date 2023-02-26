package ee.eee.testwebsock.websockets.websocket;

import ee.eee.testwebsock.websockets.data.ControlMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CarController implements CarControllerUseCase {


	private CarClient carClient;

	public CarController() {
		addNewCar();
	}


	@Override
	public void addNewCar() {
		carClient = new CarClient();
		try {
			controlCar(new ControlMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configNewCar() {

	}

	@Override
	public void releaseCar() {

	}

	@Override
	public void controlCar(ControlMessage controlMessage) throws IOException {
		carClient.sendCommand("Komenda do wysłania");
	}
}
