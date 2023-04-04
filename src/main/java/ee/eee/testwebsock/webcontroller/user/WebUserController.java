package ee.eee.testwebsock.webcontroller.user;

import ee.eee.testwebsock.webcontroller.user.requests.RegisterUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class WebUserController {

	@PostMapping
	public void register(@RequestBody RegisterUserRequest request) {

	}

}
