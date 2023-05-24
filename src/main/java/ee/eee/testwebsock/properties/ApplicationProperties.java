package ee.eee.testwebsock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.properties")
public class ApplicationProperties {
	private int tickRate = 20;
	private long maxMessageDelay = 500;
	private Long timeForRent = 3L * 60 * 1000;
	private String pathToSave = "C:\\Study\\imagesTest";

}
