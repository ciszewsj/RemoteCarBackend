package ee.eee.testwebsock.webcontroller.cars.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarRepresentationResponse {
	private Long id;
	private String carName;
	private Boolean isCarRunning;
	private Boolean isCarFree;
	private String userRentedName;
	private Long leftRentedTime;
}
