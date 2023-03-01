package ee.eee.testwebsock.websockets.data.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFrameMessage {
	private byte[] frame;
}
