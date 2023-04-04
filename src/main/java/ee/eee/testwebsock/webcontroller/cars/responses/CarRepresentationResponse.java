package ee.eee.testwebsock.webcontroller.cars.responses;

import lombok.Data;

@Data
public class CarRepresentationResponse {
	private Long id;
	private String carName;
	private Boolean isCarRunning;
	private Boolean isCarFree;
	private String userRentedName;
	private Long leftRentedTime;
}
