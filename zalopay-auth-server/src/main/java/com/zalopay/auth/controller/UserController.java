package com.zalopay.auth.controller;

import com.zalopay.auth.model.RoleName;
import com.zalopay.auth.payload.EnumRoleType;
import com.zalopay.auth.payload.LoginResponse;
import com.zalopay.auth.payload.PagedResponse;
import com.zalopay.auth.payload.User.DeleteUserRoleRequest;
import com.zalopay.auth.payload.User.UserProfilePlayload;
import com.zalopay.auth.payload.User.UserRolesForClientRequest;
import com.zalopay.auth.payload.UserSummary;
import com.zalopay.auth.security.CurrentUser;
import com.zalopay.auth.security.UserPrincipal;
import com.zalopay.auth.service.KeycloakService;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final String STRING_CONNECTOR = "___";
    private static final String RANDOM_IMAGES = "https://ui-avatars.com/api/?color=ffffff&background=FA541C&name=";

    @Value("${zaloAuth.account}")
    private String SO_ACCOUNT;

    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@CurrentUser UserPrincipal currentUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(RoleName.ROLE_ADMIN.name())) {
                isAdmin = true;
                break;
            }
        }
        String role = isAdmin ? EnumRoleType.ADMIN.name().toLowerCase() : EnumRoleType.USER.name().toLowerCase();
        return ResponseEntity.ok(new LoginResponse("ok", "account", role));
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary();
        userSummary.setName(currentUser.getName());
        userSummary.setUserid(currentUser.getId());
        userSummary.setEmail(currentUser.getEmail());
        userSummary.setUsername(currentUser.getUsername());
        return userSummary;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers() {
        ArrayList<UserProfilePlayload> users = new ArrayList<>();
        List<UserRepresentation> realmUsers = keycloakService.getUsers();
        for(UserRepresentation realmUser : realmUsers) {
          if(!SO_ACCOUNT.equals(realmUser.getUsername())) {
            users.add(new UserProfilePlayload(realmUser.getId(),
              realmUser.getUsername(),
              realmUser.getFirstName() + " " + realmUser.getLastName(),
              realmUser.getEmail(),
              realmUser.getClientRoles() != null ? realmUser.getClientRoles().toString() : "",
              realmUser.isEnabled() ? 2:3));
          }
        }
        PagedResponse res = new PagedResponse(users, users.size(),10,1);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/users/{userId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUsers(@PathVariable(value = "userId") String userId) {
        keycloakService.toggleUser(userId);
        return getUsers();
    }

    @GetMapping("/users/{userId}/logout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> logoutUser(@PathVariable(value = "userId") String userId) {
        keycloakService.logoutUsers(Arrays.asList(userId));
        return getUsers();
    }

    @PostMapping("/users/{userName}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoleForClient(@PathVariable(value = "userName") String username,
                                                     @RequestBody UserRolesForClientRequest userRolesForClientRequest) {
        keycloakService.updateUserRoleForClient(userRolesForClientRequest.getUserId(), userRolesForClientRequest.getClientId(),
          userRolesForClientRequest.getRoles());
        return getUserDetails(username);
    }

    @GetMapping("/users/{userId}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserDetails(@PathVariable(value = "userId") String userId) {
        UserRepresentation user = keycloakService.getUser(userId);

        UserSummary resUser = new UserSummary();
        resUser.setRealmId(user.getId());
        resUser.setName(user.getFirstName() + " " + user.getLastName());
        resUser.setEmail(user.getEmail());
        resUser.setUsername(user.getUsername());

        MappingsRepresentation userRolesMapping = keycloakService.getClientRoleByUserId(user.getId());

        List<ClientRepresentation> realmClients = keycloakService.getClients();
        for(ClientRepresentation client : realmClients) {
            if(!ClientController.DEFAULT_CLIENT.contains(client.getClientId())) {
                ClientMappingsRepresentation clientMapping = userRolesMapping.getClientMappings().get(client.getClientId());
                if(clientMapping != null) {
                    List<RoleRepresentation> roles = clientMapping.getMappings();
                    List<String> role = new ArrayList<>();
                    for (RoleRepresentation clientRole : roles) {
                        role.add(client.getClientId() + STRING_CONNECTOR +clientRole.getName());
                    }
                    Collections.sort(role);
                    resUser.addService(client.getId(), client.getClientId(),
                      client.getRedirectUris().isEmpty() ?"" : client.getRedirectUris().get(0).replace("*", ""),
                      RANDOM_IMAGES+client.getClientId(), role);
                } else {
                    //Dont have any role on client, still add service with empty roles
                    resUser.addService(client.getId(), client.getClientId(),
                      client.getRedirectUris().isEmpty() ?"" : client.getRedirectUris().get(0).replace("*", ""),
                      RANDOM_IMAGES+client.getClientId(), Collections.EMPTY_LIST);
                }
            }
        }

        return ResponseEntity.ok(resUser);
    }

    @PostMapping("/users/deleterole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClientRoles(@RequestBody DeleteUserRoleRequest deleteUserRoleRequest) {
        String role = deleteUserRoleRequest.getRoles().split(STRING_CONNECTOR)[1];
        keycloakService.deleteClientRoles(deleteUserRoleRequest.getUserId(),
          deleteUserRoleRequest.getClientId(), role);
        return getUserDetails(deleteUserRoleRequest.getUserName());
    }
}
