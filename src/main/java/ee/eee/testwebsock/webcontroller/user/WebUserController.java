package ee.eee.testwebsock.webcontroller.user;

import ee.eee.testwebsock.webcontroller.user.requests.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class WebUserController {
	private final RealmResource realm;

	@PostMapping
	public void register(@RequestBody RegisterUserRequest request) {
		UserRepresentation userRepresentation = new UserRepresentation();

		userRepresentation.setUsername(request.getName());
		userRepresentation.setEmail(request.getEmail());
		userRepresentation.setEnabled(true);
		userRepresentation.setEmailVerified(true);

		Response response = realm.users().create(userRepresentation);
		CredentialRepresentation passwordCred = new CredentialRepresentation();
		String userId = CreatedResponseUtil.getCreatedId(response);

		passwordCred.setTemporary(false);
		passwordCred.setType("password");
		passwordCred.setValue(request.getPassword());
		UserResource userResource = realm.users().get(userId);
		userResource.resetPassword(passwordCred);


		RoleRepresentation roleRepresentationList =
				userResource.roles().realmLevel().listAvailable().stream()
						.filter(roleRepresentation -> roleRepresentation.getName().contains("app_user")).findFirst().orElseThrow();
		userResource.roles().realmLevel().add(List.of(roleRepresentationList));
	}

}
