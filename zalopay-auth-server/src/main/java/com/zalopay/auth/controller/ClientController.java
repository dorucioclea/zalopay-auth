package com.zalopay.auth.controller;

import com.zalopay.auth.model.RoleName;
import com.zalopay.auth.payload.Client.ClientPayload;
import com.zalopay.auth.payload.Client.CreateClientRoleRequest;
import com.zalopay.auth.payload.Client.RoleOfClientResponse;
import com.zalopay.auth.security.CurrentUser;
import com.zalopay.auth.security.UserPrincipal;
import com.zalopay.auth.service.KeycloakService;
import com.zalopay.auth.service.KongService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.representations.idm.ClientMappingsRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private KongService kongService;

    public static final List<String> DEFAULT_CLIENT = Arrays.asList("account",
      "admin-cli", "broker", "realm-management", "security-admin-console", "kong");

    private static final String RANDOM_IMAGES = "https://ui-avatars.com/api/?color=ffffff&background=FA541C&name=";

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @PostMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClient(@RequestBody ClientPayload client,
                                          @CurrentUser UserPrincipal currentUser) {
        if("".equals(client.getId())) {
            keycloakService.createClient(client);
            kongService.createApi(client);
            return getClients(currentUser);
        }
        if("delete".equals(client.getMethod())) {
            return deleteClient(client, currentUser);
        }

        keycloakService.updateClient(client);
        kongService.updateApi(client);
        return getClients(currentUser);
    }

    @GetMapping("/clients/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getClientRoles(@RequestParam String clientId) {
        List<RoleRepresentation> roles = keycloakService.getClientRoles(clientId);
        List<RoleOfClientResponse> rolesResponse = new ArrayList<>();
        for(RoleRepresentation role : roles) {
            rolesResponse.add(new RoleOfClientResponse(role.getId(), role.getName(), role.getDescription()));
        }
        return ResponseEntity.ok(rolesResponse);
    }

    @PostMapping("/clients/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRoleForClient(@RequestBody CreateClientRoleRequest roleForClientRequest) {
        keycloakService.createRoleForClient(roleForClientRequest.getClientId(), roleForClientRequest.getName(),
          roleForClientRequest.getDescription());
        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClient(ClientPayload client, @CurrentUser UserPrincipal currentUser) {
        kongService.deleteService(client);
        keycloakService.deleteClient(client.getId());
        return getClients(currentUser);
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getClients(@CurrentUser UserPrincipal currentUser) {
        ArrayList<ClientPayload> res = new ArrayList<>();
        List<ClientRepresentation> realmClients = keycloakService.getClients();
        JSONArray kongRoutes = kongService.getRoutes();
        JSONArray kongPlugins = kongService.getPlugins();
        JSONArray kongServices = kongService.getServices();

        // Get permission of user
        boolean isAdmin = false;
        for (GrantedAuthority grantedAuthority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (RoleName.ROLE_ADMIN.name().equals(grantedAuthority.getAuthority())) {
                isAdmin = true;
                break;
            }
        }

        for(ClientRepresentation client : realmClients) {
            ClientPayload clientPayload = new ClientPayload();
            if(!DEFAULT_CLIENT.contains(client.getClientId())) {
                clientPayload.setId(client.getId());
                clientPayload.setDescription(client.getName());
                List<RoleRepresentation> roles = keycloakService.getClientRoles(client.getId());
                List<String> clientRolres = new ArrayList<>();
                for(RoleRepresentation role : roles) {
                    clientRolres.add(role.getName());
                }

                clientPayload.setRoles(Arrays.asList(clientRolres.toString()
                  .replace("[", "")
                  .replace("]", "").split(",")));
                clientPayload.setStatus("active");
                clientPayload.setSubDescription(client.getDescription());
                clientPayload.setTitle(client.getName());
                clientPayload.setAvatar(RANDOM_IMAGES+client.getClientId());

                JSONObject service = findServiceByClientId(kongServices, client.getClientId());
                if (service != null && service != JSONObject.NULL) {
                    String host = service.get("host") != JSONObject.NULL ? (String) service.get("host") : "";
                    int port = service.get("port") != JSONObject.NULL ? (int) service.get("port") : null;
                    String path = service.get("path") != JSONObject.NULL ? (String) service.get("path") : "";
                    clientPayload.setHref("http://" + host + ":" + port + path);
                } else {
                    clientPayload.setHref("");
                }

                // Find route and plugin by clientId
                JSONObject route = findRouteByClientId(kongRoutes, client.getClientId());
                if (route != null) {
                    JSONArray paths = (JSONArray) route.get("paths");
                    clientPayload.setPath(paths.get(0).toString());
                    clientPayload.setRouteId((String) route.get("id"));
                    JSONObject serviceOnRoute = (JSONObject) route.get("service");
                    clientPayload.setServiceId((String) serviceOnRoute.get("id"));

                    JSONObject plugin = findOIDCPluginByClientId(kongPlugins, (String) serviceOnRoute.get("id"));
                    if(plugin != JSONObject.NULL) {
                        clientPayload.setOidc(true);
                    } else {
                        clientPayload.setOidc(false);
                    }
                } else {
                    clientPayload.setPath("");
                    clientPayload.setRouteId("");
                    clientPayload.setServiceId("");
                    clientPayload.setOidc(false);
                }

                // Check if user and has no role on client and is not admin, do not display any service
                if (checkUserHasRoleOnClient(currentUser.getRealmId(), client.getClientId()) || isAdmin) {
                    res.add(clientPayload);
                }
            }
        }
        return ResponseEntity.ok(res);
    }

    private JSONObject findRouteByClientId(JSONArray kongRoutes, String clientId) {
        for(Object route : kongRoutes) {
            JSONObject obj = (JSONObject) route;
            if((clientId + "-routes").equals( obj.get("name"))) {
                return obj;
            }
        }
        return null;
    }

    private JSONObject findOIDCPluginByClientId(JSONArray kongPlugins, String serviceId) {
        for(Object plugin : kongPlugins) {
            JSONObject obj = (JSONObject) plugin;
            if (obj.get("service") != JSONObject.NULL && obj.get("name") != JSONObject.NULL) {
                JSONObject serviceObj = (JSONObject) obj.get("service");
                if("oidc".equals(obj.get("name")) && serviceId.equals(serviceObj.get("id"))) {
                    return obj;
                }
            }
        }
        return null;
    }

    private JSONObject findServiceByClientId(JSONArray kongServices, String clientId) {
        for(Object service : kongServices) {
            JSONObject obj = (JSONObject) service;
            if(clientId.equals( obj.get("name"))) {
                return obj;
            }
        }
        return null;
    }

    private boolean checkUserHasRoleOnClient(String userId, String clientId) {
        MappingsRepresentation userRolesMapping = keycloakService.getClientRoleByUserId(userId);
        ClientMappingsRepresentation clientMapping = userRolesMapping.getClientMappings().get(clientId);
        if(clientMapping != null) {
            return true;
        }
        return false;
    }
}
