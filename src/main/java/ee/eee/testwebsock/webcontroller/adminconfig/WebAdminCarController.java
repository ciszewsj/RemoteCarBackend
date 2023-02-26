package ee.eee.testwebsock.webcontroller.adminconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/car_admin")
public class WebAdminCarController {

	@GetMapping
	public void getCars() {

	}

	@GetMapping("/{id}")
	public void getCar(@PathVariable String id) {

	}

	@PostMapping
	public void addCar() {

	}

	@PutMapping("/{id}")
	public void updateCar(@PathVariable String id) {

	}

	@DeleteMapping("/{id}")
	public void deleteCar(@PathVariable String id) {

	}
}
