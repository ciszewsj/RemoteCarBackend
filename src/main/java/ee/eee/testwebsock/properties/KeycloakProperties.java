package ee.eee.testwebsock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.keycloak")
public class KeycloakProperties {
	String serverUrl = "http://localhost:8080";
	String realm = "master";
	String clientId = "admin-cli";
	String grantType = "password";
	String username = "admin";
	String password = "admin";

	String mainRealm = "SpringBootKeycloak";
}
