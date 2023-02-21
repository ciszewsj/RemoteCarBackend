package ee.eee.testwebsock.webcontroller.adminconfig;

import lombok.Data;

@Data
public class CarConfig {
	private Integer id;

	private String name;

	private ConnectionStatus status;

	public enum ConnectionStatus {
		CONNECTED, DISCONNECTED
	}
}
