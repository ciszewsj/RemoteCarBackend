package ee.eee.testwebsock.config;

import ee.eee.testwebsock.properties.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakAdminConfig {

	private final KeycloakProperties properties;

	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder()
				.serverUrl(properties.getServerUrl())
				.realm(properties.getRealm())
				.clientId(properties.getClientId())
				.grantType(properties.getGrantType())
				.username(properties.getUsername())
				.password(properties.getPassword())
				.build();
	}

	@Bean
	public RealmResource realm(Keycloak keycloak) {
		return keycloak.realm(properties.getMainRealm());
	}
}
