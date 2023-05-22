package ee.eee.testwebsock.config;

import ee.eee.testwebsock.utils.MyPrincipalJwtConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	public static final String ADMIN = "admin";
	public static final String USER = "user";
	private final MyPrincipalJwtConvertor jwtAuthConverter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
				.authorizeHttpRequests()
				.requestMatchers("/car_admin/**", "/car_admin").hasRole(ADMIN)
				.requestMatchers("/car/**", "/car").hasAnyRole(ADMIN, USER)
				.anyRequest().permitAll();
		http.oauth2ResourceServer()
				.jwt()
				.jwtAuthenticationConverter(jwtAuthConverter);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
	}
}
