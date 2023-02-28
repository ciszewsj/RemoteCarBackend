package ee.eee.testwebsock.database;

import ee.eee.testwebsock.database.data.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarEntityRepository extends JpaRepository<CarEntity, Long> {
}
