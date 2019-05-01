package com.zalopay.auth.repository;

import com.zalopay.auth.exception.AppException;
import com.zalopay.auth.payload.Client.ClientPayload;
import com.zalopay.auth.security.KeyCloakConnectionProvider;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeycloakRepository {

  @Value("${keycloak.realm}")
  private String REALM_NAME;

  @Value("${zaloAuth.homepageurl}")
  private String REDIRECT_URL;

  Keycloak keycloak = KeyCloakConnectionProvider.getConnection();

  /********************************************
   * Keycloak User
   *******************************************/
  public List<UserRepresentation> getUsers() {
    if (keycloak != null) {
      return keycloak.realm(REALM_NAME).users().list();
    }
    return Collections.EMPTY_LIST;
  }

  public UserRepresentation getUser(String userName) {
    if (keycloak != null) {
      return keycloak.realm(REALM_NAME).users().search(userName).get(0);
    }
    return null;
  }

  public void updateUserRoleForClient(String userId, String clientId, List<String> roles) {
    UserResource userResource = keycloak.realm(REALM_NAME).users().get(userId);
    List<RoleRepresentation> rolesList = new ArrayList<>();
    for(String role : roles) {
      RoleRepresentation clientRole = keycloak.realm(REALM_NAME).clients()
        .get(clientId)
        .roles()
        .get(role.trim())
        .toRepresentation();
      rolesList.add(clientRole);
    }
    userResource.roles().clientLevel(clientId).add(rolesList);
  }

  public void deleteClientRoles(String userRealmId, String clientId, String role) {
    if (keycloak != null) {
      List<RoleRepresentation> rolesList = new ArrayList<>();
      UserResource userResource = keycloak.realm(REALM_NAME).users().get(userRealmId);

      RoleRepresentation clientRole = keycloak.realm(REALM_NAME).clients()
        .get(clientId)
        .roles()
        .get(role)
        .toRepresentation();

      rolesList.add(clientRole);

      userResource.roles().clientLevel(clientId).remove(rolesList);
    }
  }

  public List<RoleRepresentation> getClientRoles(String clientId) {
    return keycloak.realm(REALM_NAME)
      .clients()
      .get(clientId)
      .roles()
      .list();
  }

  public void updateUser(String userId) {
    if (keycloak != null) {
      UserResource userResource = keycloak.realm(REALM_NAME).users().get(userId);
      UserRepresentation user = userResource.toRepresentation();
      user.setEmail(user.getEmail());
      userResource.update(user);
    }
  }

  public void toggleUser(String userId) {
    if (keycloak != null) {
      UserResource userResource = keycloak.realm(REALM_NAME).users().get(userId);
      UserRepresentation user = userResource.toRepresentation();
      user.setEnabled(!user.isEnabled());
      userResource.update(user);
      userResource.logout();
    }
  }

  public void logoutUser(String userId) throws AppException {
    if(keycloak != null) {
      UserResource userResource = keycloak.realm(REALM_NAME).users().get(userId);
      userResource.logout();
    }
  }

  /********************************************
   * Keycloak Client
   *******************************************/
  public List<ClientRepresentation> getClients() {
    if (keycloak != null) {
      return keycloak.realm(REALM_NAME).clients().findAll();
    }
    return Collections.EMPTY_LIST;
  }

  public void deleteClient(String clientId) {
    if (keycloak != null) {
      keycloak.realm(REALM_NAME).clients().get(clientId).remove();
    }
  }

  private ClientRepresentation clientMapping(ClientPayload client) {
    ClientRepresentation clientMapping = new ClientRepresentation();
    clientMapping.setClientId(client.getTitle());
    clientMapping.setName(client.getTitle());
    clientMapping.setRedirectUris(Arrays.asList(REDIRECT_URL));
    clientMapping.setDefaultRoles(client.getRoles().toArray(new String[0]));
    clientMapping.setDescription(client.getSubDescription());
    return clientMapping;
  }

  public void updateClient(ClientPayload client) {
    if (keycloak != null) {
      String clientId = client.getId();
      ClientRepresentation updatedClient = clientMapping(client);
      List<RoleRepresentation> currentRoleList = keycloak.realm(REALM_NAME).clients().get(clientId).roles().list();
      for (RoleRepresentation role : currentRoleList) {
        if(client.getRoles().size()> 0) {
          // Remove the role which is no longer existing on keycloak
          if(!client.getRoles().contains(role.getName())) {
            keycloak.realm(REALM_NAME).clients().get(clientId).roles().deleteRole(role.getName());
          } else {
            // Remove the role which is existing on keycloak
            client.getRoles().remove(role.getName());
          }

        }
      }
      updatedClient.setDefaultRoles(client.getRoles().toArray(new String[0]));
      keycloak.realm(REALM_NAME).clients().get(clientId).update(updatedClient);
    }
  }

  public void createClient(ClientPayload client) {
    if (keycloak != null) {
      keycloak.realm(REALM_NAME).clients().create(clientMapping(client));
    }
  }

  public void createRoleForClient(String clientId, String roleName, String description) {
    if (keycloak != null) {
      RoleRepresentation newRole = new RoleRepresentation();
      newRole.setName(roleName);
      newRole.setDescription(description);
      keycloak.realm(REALM_NAME).clients().get(clientId).roles().create(newRole);
    }
  }

  public MappingsRepresentation getClientRoleByUserId(String userId) {
    if (keycloak != null) {
      return keycloak.realm(REALM_NAME).users().get(userId).roles().getAll();
    }
    return null;
  }
}
