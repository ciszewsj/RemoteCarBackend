package ee.eee.testwebsock.websockets.websocket.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.eee.testwebsock.websockets.data.ControlMessage;
import ee.eee.testwebsock.websockets.data.car.CarConfigMessage;
import ee.eee.testwebsock.websockets.data.car.CarControlMessage;
import ee.eee.testwebsock.websockets.websocket.user.UserControllerUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CarController implements CarControllerUseCase {


	private final int tickRate = 20;

	private boolean isRunning = true;

	private CarClient carClient;


	private final UserControllerUseCase userControllerUseCase;

	private final ObjectMapper objectMapper;

	public CarController(UserControllerUseCase userControllerUseCase) {
		this.objectMapper = new ObjectMapper();
		this.userControllerUseCase = userControllerUseCase;
		addNewCar();
	}


	@Override
	public void addNewCar() {
		carClient = new CarClient();
		controlCar(new ControlMessage());
		controlFunction();
	}

	@Override
	public void configCar() throws IOException {
		CarControlMessage<CarConfigMessage> carControlMessage =
				new CarControlMessage<>(
						CarControlMessage.CarControlMessageType.CONFIG_MESSAGE,
						CarConfigMessage.builder()
								.fps(30)
								.build()
				);
		carClient.sendCommand(objectMapper.writeValueAsString(carControlMessage));
	}

	@Override
	public void releaseCar() {
		isRunning = false;
	}

	@Override
	public void controlCar(ControlMessage controlMessage) {
		log.error(controlMessage.toString());
//		this.lastControlMessage = controlMessage;
	}

	private ScheduledFuture<?> controlFunction() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		return executor.scheduleAtFixedRate(() -> {
			try {
				carClient.sendCommand(new ClassPathResource("kolo.png").getInputStream().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
	}
}
