package ee.eee.testwebsock.database.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
public class CarStatusEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Getter
	private Date date = new Date();

	@Getter
	private Status status;

	public enum Status {
		CREATED,
		LOADED,
		CONNECTED,
		DISCONNECTED,
		CONNECTION_FAILURE,
		CONFIGURE

	}
}
