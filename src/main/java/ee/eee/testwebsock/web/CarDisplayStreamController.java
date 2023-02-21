package ee.eee.testwebsock.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CarDisplayStreamController {

	private boolean is = true;

	@Autowired
	public CarDisplayStreamController(WebSocketCarHandler webSocketCarHandler) {
		Runnable a = () -> {
			while (is) {
				try {
					webSocketCarHandler.sendImageToAllUsers(null);
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(a);
		t.start();
	}
}
