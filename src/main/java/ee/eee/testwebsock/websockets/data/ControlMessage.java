package ee.eee.testwebsock.websockets.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ControlMessage {
	private Double verticalSpeed = 0.0;
	private Double horizontalSpeed = 0.0;
}
