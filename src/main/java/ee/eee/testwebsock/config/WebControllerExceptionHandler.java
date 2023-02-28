package ee.eee.testwebsock.config;

import ee.eee.testwebsock.utils.WebControllerException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebControllerExceptionHandler {

	@ExceptionHandler(WebControllerException.class)
	public ResponseEntity<?> handleException(WebControllerException exception) {
		return ResponseEntity.status(exception.getExceptionStatus().getHttpStatus())
				.body(
						ErrorResponse.builder()
								.message(exception.getExceptionStatus().name())
								.code(exception.getExceptionStatus().getCode())
								.build()
				);
	}

	@Builder
	@Data
	public static class ErrorResponse {
		String message;
		String code;
	}
}
