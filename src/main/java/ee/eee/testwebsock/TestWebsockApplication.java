package ee.eee.testwebsock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class TestWebsockApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestWebsockApplication.class, args);
	}

}
