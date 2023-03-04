package ee.eee.testwebsock.database;

import ee.eee.testwebsock.database.data.CarEntity;
import ee.eee.testwebsock.database.data.CarStatusEntity;
import ee.eee.testwebsock.utils.WebControllerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Component
@Transactional
@RequiredArgsConstructor
public class CarImplService {
	private final CarEntityRepository carEntityRepository;

	public void addCarStatus(Long carId, CarStatusEntity.Status status) {
		CarEntity car = carEntityRepository.findById(carId).orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));

		CarStatusEntity statusEntity = new CarStatusEntity();

		statusEntity.setStatus(status);

		car.getCarStatusEntityList().add(statusEntity);
	}

	public void turnCarOff(Long carId) {
		CarEntity car = carEntityRepository.findById(carId).orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));
		car.setStatus(CarEntity.ConnectionStatus.DISCONNECTED);
	}

	public void turnCarOn(Long carId) {
		CarEntity car = carEntityRepository.findById(carId).orElseThrow(new WebControllerException(WebControllerException.ExceptionStatus.CAR_NOT_FOUND));
		car.setStatus(CarEntity.ConnectionStatus.CONNECTED);
	}
}
