package ee.eee.testwebsock.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public class WebControllerException extends RuntimeException implements Supplier<RuntimeException> {

	@Getter
	private final ExceptionStatus exceptionStatus;

	public WebControllerException(ExceptionStatus exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}

	@Override
	public RuntimeException get() {
		return this;
	}


	public enum ExceptionStatus {
		CAR_NOT_FOUND(HttpStatus.NOT_FOUND, "E001"),
		CAR_IS_RUNNING(HttpStatus.CONFLICT, "E002"),
		CAR_IS_NOT_RUNNING(HttpStatus.CONFLICT, "E003"),
		COULD_NOT_CONFIGURE_CAR_WHILE_RUNNING(HttpStatus.CONFLICT, "E004"),
		CAR_IS_RENTED(HttpStatus.CONFLICT, "E005"),
		COULD_NOT_TAKE_CONTROL(HttpStatus.CONFLICT, "E006"),

		CAR_START_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E010"),
		CAR_STOP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E011"),
		CAR_NOT_FOUND_IN_CONTROLLER(HttpStatus.INTERNAL_SERVER_ERROR, "E012"),

		COULD_NOT_REGISTER_USER(HttpStatus.CONFLICT, "E99");
		@Getter
		private final HttpStatus httpStatus;

		@Getter
		private final String code;

		ExceptionStatus(HttpStatus httpStatus, String code) {
			this.httpStatus = httpStatus;
			this.code = code;

		}

	}
}
