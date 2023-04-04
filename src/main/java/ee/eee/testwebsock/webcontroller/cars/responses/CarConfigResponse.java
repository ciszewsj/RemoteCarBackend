package ee.eee.testwebsock.webcontroller.cars.responses;

import ee.eee.testwebsock.utils.ImageObject;
import lombok.Data;

import java.util.List;

@Data
public class CarConfigResponse {
	public Long carRentTime = 0L;
	public List<ImageObject.ImageSize> imageSizes = List.of(ImageObject.ImageSize.values());
}
