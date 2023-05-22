package ee.eee.testwebsock.utils;

import ee.eee.testwebsock.properties.JwtAuthConverterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyPrincipalJwtConvertor implements Converter<Jwt, AbstractAuthenticationToken> {

	private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	private final JwtAuthConverterProperties properties;


	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {

		Collection<GrantedAuthority> authorities = Stream.concat(
				jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
				extractResourceRoles(jwt).stream()).collect(Collectors.toSet());

		CustomAuthenticationObject customAuthenticationObject = new CustomAuthenticationObject(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), jwt.getClaims());
		return new JwtAuthenticationToken(customAuthenticationObject, authorities, getPrincipalClaimName(jwt));
	}


	private String getPrincipalClaimName(Jwt jwt) {
		String claimName = JwtClaimNames.SUB;
		if (properties.getPrincipalAttribute() != null) {
			claimName = properties.getPrincipalAttribute();
		}
		return jwt.getClaim(claimName);
	}

	private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
		Map<String, Object> resource;
		Collection<String> resourceRoles;
		if ((resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
				|| (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
			return Set.of();
		}
		return resourceRoles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toSet());
	}
}


