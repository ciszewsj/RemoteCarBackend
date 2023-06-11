package ee.eee.testwebsock.websockets.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlMessage {
	private Double verticalSpeed = 0.0;
	private Double horizontalSpeed = 0.0;

	public static ControlMessage normalizeControlMessage(ControlMessage controlMessage) {
		return ControlMessage.builder()
				.horizontalSpeed(controlMessage.getHorizontalSpeed() > 1 || controlMessage.getHorizontalSpeed() < -1 ? controlMessage.getHorizontalSpeed() % Math.abs(controlMessage.getHorizontalSpeed()) : controlMessage.getHorizontalSpeed())
				.verticalSpeed(controlMessage.getVerticalSpeed() > 1 || controlMessage.getVerticalSpeed() < -1 ? controlMessage.getVerticalSpeed() % Math.abs(controlMessage.getVerticalSpeed()) : controlMessage.getVerticalSpeed())
				.build();
	}

}
