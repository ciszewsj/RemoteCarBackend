package ee.eee.testwebsock.database.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class CarStatusEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Date date;

	private Status status;

	public enum Status {
		CREATED,
		CONNECTED,
		DISCONNECTED,
		CONNECTION_FAILURE,
		SEND_CONFIG

	}
}