package ee.eee.testwebsock.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class CustomAuthenticationObject extends Jwt implements Principal {

	private String id;
	private String name;
	private String email;

	public CustomAuthenticationObject(String tokenValue, Instant issuedAt, Instant expiresAt, Map<String, Object> headers, Map<String, Object> claims) {
		super(tokenValue, issuedAt, expiresAt, headers, claims);

		this.email = claims.get("email").toString();
		this.id = claims.get("sid").toString();
		this.name = claims.get("name").toString();
	}

	@Override
	public String getName() {
		return this.name;
	}
}