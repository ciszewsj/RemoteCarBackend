package ee.eee.testwebsock.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	public static final String ADMIN = "admin";
	public static final String USER = "user";
	private final JwtAuthConverter jwtAuthConverter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
				.requestMatchers("/**", "/test/anonymous", "/test/anonymous/**").permitAll()
				.requestMatchers("/test/auth", "/test/auth/**").authenticated()
				.requestMatchers("/test/admin", "/test/admin/**").hasRole(ADMIN)
				.requestMatchers("/test/user").hasAnyRole(ADMIN, USER);
//				.anyRequest().permitAll();
		http.oauth2ResourceServer()
				.jwt()
				.jwtAuthenticationConverter(jwtAuthConverter);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
	}

}
