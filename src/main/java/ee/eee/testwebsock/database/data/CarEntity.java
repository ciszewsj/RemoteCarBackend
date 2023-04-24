package ee.eee.testwebsock.database.data;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class CarEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String url;

	private String pictureUrl;

	private Integer fps;

	private ConnectionStatus status = CarEntity.ConnectionStatus.DISCONNECTED;

	@ManyToMany
	private List<CarStatusEntity> carStatusEntityList = new ArrayList<>();

	public enum ConnectionStatus {
		CONNECTED, DISCONNECTED
	}
}
