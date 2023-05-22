package ee.eee.testwebsock.webcontroller.user.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
	@Size(min = 3, max = 40)
	@NotEmpty
	private String name;
	@Email
	@NotEmpty
	private String email;
	@Size(min = 3, max = 40)
	@NotEmpty
	private String password;
}
