package ee.eee.testwebsock.websockets.data.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserControlMessage<T> {
	private UserControlMessageType type;
	private T data;

	public enum UserControlMessageType {
		CONTROL_MESSAGE,
		DISPLAY_MESSAGE,
		INFO_MESSAGE,
		GET_CONTROL,
		CONFIG_MESSAGE
	}
}
