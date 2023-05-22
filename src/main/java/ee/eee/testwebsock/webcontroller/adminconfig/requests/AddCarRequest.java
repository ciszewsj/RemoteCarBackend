package ee.eee.testwebsock.webcontroller.adminconfig.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddCarRequest {
	@NotEmpty
	@Size(min = 1, max = 256)
	private final String name;
	@NotEmpty
	@Size(min = 1, max = 256)
	private final String url;
}
