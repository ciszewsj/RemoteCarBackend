package ee.eee.testwebsock.webcontroller.cars.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarRentRequest {
	@NotNull
	private String websocketId;
}
