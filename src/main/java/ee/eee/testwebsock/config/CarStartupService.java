package ee.eee.testwebsock.config;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.websockets.websocket.car.CarControllerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarStartupService {

	private final CarEntityRepository carRepository;
	private final CarControllerUseCase carControllerUseCase;

	@EventListener(ApplicationReadyEvent.class)
	public void initCars() {
		log.info("Init existing cars");
		carRepository.findAll()
				.forEach(
						car -> {
							try {
								carControllerUseCase.addNewCar(car);
								if (car.getStatus().equals(CarEntity.ConnectionStatus.CONNECTED)) {
									carControllerUseCase.connectCar(car.getId());
								}
							} catch (Exception e) {
								log.error(e.toString(), e);
							}
						}
				);
	}
}
