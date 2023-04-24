package ee.eee.testwebsock.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class KeycloakAdminConfig {
	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder()
				.serverUrl("http://localhost:8080")
				.realm("master")
				.clientId("admin-cli")
				.grantType("password")
				.username("admin")
				.password("admin")
				.build();
	}

	@Bean
	public RealmResource realm(Keycloak keycloak) {
		return keycloak.realm("SpringBootKeycloak");
	}
}
