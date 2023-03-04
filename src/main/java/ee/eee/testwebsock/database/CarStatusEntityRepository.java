package ee.eee.testwebsock.database;

import ee.eee.testwebsock.database.data.CarStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarStatusEntityRepository extends JpaRepository<CarStatusEntity, Long> {
}
