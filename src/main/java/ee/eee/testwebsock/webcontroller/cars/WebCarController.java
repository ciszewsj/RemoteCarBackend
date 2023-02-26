package ee.eee.testwebsock.webcontroller.cars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/car")
public class WebCarController {

	@GetMapping
	public void getCars() {

	}

	@GetMapping("/{id}")
	public void getCar(@PathVariable String id) {

	}

	@PostMapping
	public void rentCar() {

	}
}
