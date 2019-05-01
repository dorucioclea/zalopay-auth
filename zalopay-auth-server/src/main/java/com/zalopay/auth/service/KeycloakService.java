package com.zalopay.auth.service;

import com.zalopay.auth.payload.Client.ClientPayload;
import com.zalopay.auth.repository.KeycloakRepository;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakService {

  @Autowired
  private KeycloakRepository keycloakRepository;

  private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

  /********************************************
   * Keycloak User
   *******************************************/
  public List<UserRepresentation> getUsers() {
    return keycloakRepository.getUsers();
  }

  public UserRepresentation getUser(String userId) {
    return keycloakRepository.getUser(userId);
  }

  public void updateUsers() {
    keycloakRepository.updateUser("40d94da4-667d-4c94-ad19-42c204923167");
  }

  public void updateUserRoleForClient(String userId, String clientId, List<String> roles) {
    keycloakRepository.updateUserRoleForClient(userId, clientId, roles);
  }

  public void logoutUsers(List<String> userIds) {
    if (userIds.isEmpty()) {
      return;
    }
    for (String userId : userIds) {
      keycloakRepository.logoutUser(userId);
    }
  }

  public void toggleUser(String userId) {
    keycloakRepository.toggleUser(userId);
  }

  public void deleteClientRoles(String userRealmId, String clientId, String role) {
    keycloakRepository.deleteClientRoles(userRealmId, clientId, role);
  }

  /********************************************
   * Keycloak Client
   *******************************************/
  public List<ClientRepresentation> getClients() {
    return keycloakRepository.getClients();
  }

  public List<RoleRepresentation> getClientRoles(String clientId) {
    return keycloakRepository.getClientRoles(clientId);
  }

  public void createClient(ClientPayload client) {
    keycloakRepository.createClient(client);
  }

  public void updateClient(ClientPayload client) {
    keycloakRepository.updateClient(client);
  }

  public void createRoleForClient(String clientId, String roleName, String roleDescription) {
    keycloakRepository.createRoleForClient(clientId, roleName, roleDescription);
  }

  public MappingsRepresentation getClientRoleByUserId(String userId) {
    return keycloakRepository.getClientRoleByUserId(userId);
  }

  public void deleteClient(String clientId) {
    keycloakRepository.deleteClient(clientId);
  }

}
