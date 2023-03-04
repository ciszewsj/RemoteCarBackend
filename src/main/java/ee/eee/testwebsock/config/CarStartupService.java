package ee.eee.testwebsock.config;

import ee.eee.testwebsock.database.CarEntityRepository;
import ee.eee.testwebsock.database.CarImplService;
import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarStatusEntity;
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
	private final CarImplService carImplService;

	@EventListener(ApplicationReadyEvent.class)
	public void initCars() {
		log.info("Init existing cars");
		carRepository.findAll()
				.forEach(
						car -> {
							carImplService.addCarStatus(car.getId(), CarStatusEntity.Status.LOADED);
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
