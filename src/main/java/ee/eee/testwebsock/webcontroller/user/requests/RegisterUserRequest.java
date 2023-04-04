package ee.eee.testwebsock.webcontroller.user.requests;

import lombok.Data;

@Data
public class RegisterUserRequest {
	private String name;
	private String email;
	private String password;
}
