package ee.eee.testwebsock.registerTest;

import ee.eee.testwebsock.properties.KeycloakProperties;
import ee.eee.testwebsock.webcontroller.user.WebUserController;
import ee.eee.testwebsock.webcontroller.user.requests.RegisterUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@Slf4j
public class RegisterTestController {

	RealmResource resource;
	KeycloakProperties properties;
	Response response;
	UserResource userResource;
	RoleMappingResource mappingResource;
	RoleScopeResource scopeResource;
	RoleRepresentation roleRepresentation;

	UsersResource usersResource;
	WebUserController controller;

	@BeforeEach
	public void beforeTest() {
		resource = Mockito.mock(RealmResource.class);
		properties = Mockito.mock(KeycloakProperties.class);
		usersResource = Mockito.mock(UsersResource.class);
		response = Mockito.mock(Response.class);
		userResource = Mockito.mock(UserResource.class);
		mappingResource = Mockito.mock(RoleMappingResource.class);
		scopeResource = Mockito.mock(RoleScopeResource.class);
		roleRepresentation = Mockito.mock(RoleRepresentation.class);

		Mockito.when(resource.users()).thenReturn(usersResource);
		Mockito.when(usersResource.create(any())).thenReturn(response);
		Mockito.when(response.getLocation()).thenReturn(URI.create("???"));
		Mockito.when(usersResource.get(any())).thenReturn(userResource);
		Mockito.when(userResource.roles()).thenReturn(mappingResource);
		Mockito.when(mappingResource.realmLevel()).thenReturn(scopeResource);
		Mockito.when(roleRepresentation.getName()).thenReturn("app_user");
		Mockito.when(scopeResource.listAvailable()).thenReturn(List.of(roleRepresentation));

		MockedStatic<CreatedResponseUtil> utilities = Mockito.mockStatic(CreatedResponseUtil.class);
		utilities.when(() -> CreatedResponseUtil.getCreatedId(any())).thenReturn("ID");


		controller = new WebUserController(resource, properties);
	}

	@Test
	public void registerNewUserTest() {
		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		registerUserRequest.setEmail("email@o2.pl");
		registerUserRequest.setName("new_user");
		registerUserRequest.setPassword("123456");

		controller.register(registerUserRequest);
	}
}
