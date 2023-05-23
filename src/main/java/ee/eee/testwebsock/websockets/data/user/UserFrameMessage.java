package ee.eee.testwebsock.websockets.data.user;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserFrameMessage {
	private byte[] frame;
	private Long timeToEnd;
	private String sessionSteeringId;
	private String userRentId;
}
