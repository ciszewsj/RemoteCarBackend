package ee.eee.testwebsock.websockets.data;

import lombok.Data;

@Data
public class ControlMessage {
	private Double verticalSpeed;
	private Double horizontalSpeed;
}
