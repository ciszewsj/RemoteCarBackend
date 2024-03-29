package ee.eee.testwebsock.websockets.data.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoMessage {
	private UserInfoType msg;

	private String websocketId;

	public enum UserInfoType {
		CONNECTED_SUCCESSFULLY,
		CAR_NOT_EXISTS,
		CAR_DISCONNECTED,
		GET_CONTROL_POSSIBILITY,
		CONTROL_GRANTED,
		CONTROL_REFUSED
	}
}
