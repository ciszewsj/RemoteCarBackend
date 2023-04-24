package ee.eee.testwebsock.webcontroller.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
	@Size(min = 3, max = 40)
	private String name;
	@Email
	private String email;
	@Size(min = 3, max = 40)
	private String password;
}
