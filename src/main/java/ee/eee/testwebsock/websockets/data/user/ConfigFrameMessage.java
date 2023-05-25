package ee.eee.testwebsock.websockets.data.user;

import ee.eee.testwebsock.utils.ImageObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigFrameMessage {
	private ImageObject.ImageSize size;
}
