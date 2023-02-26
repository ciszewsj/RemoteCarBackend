package ee.eee.testwebsock.websockets.data.car;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarConfigMessage {
	private int fps;
}
