package ee.eee.testwebsock.webcontroller.adminconfig.requests;

import lombok.Data;

@Data
public class AddCarRequest {
	private final String name;
	private final String url;
	private final Integer fps;
}
