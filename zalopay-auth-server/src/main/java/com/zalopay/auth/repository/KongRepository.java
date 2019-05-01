package com.zalopay.auth.repository;

import com.zalopay.auth.payload.Client.ClientPayload;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KongRepository {

  @Value("${kong.url}")
  private String KONG_URL;

  @Value("${kong.config.discovery}")
  private String KONG_CONFIG_DISCOVERY;

  @Value("${kong.config.client_secret}")
  private String KONG_CONFIG_CLIENT_SECRET;

  @Value("${kong.config.client_id}")
  private String KONG_CONFIG_CLIENT_ID;

  private static final String KONG_SERVICES = "services";

  private static final String KONG_ROUTES  = "routes";

  private static final String KONG_PLUGINS  = "plugins";

  private static final String KONG_CORS_PLUGIN  = "cors";

  private static final String KONG_OIDC_PLUGIN  = "oidc";

  private static final String KONG_HTTP_LOG_PLUGIN  = "http-log";

  public void createService(ClientPayload client) {
    CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(KONG_URL + KONG_SERVICES)
      .header("accept", "application/json")
      .field("name", client.getTitle())
      .field("url", client.getHref())
      .asJsonAsync(response -> {
        int code = response.getStatus();
        if (code >= 200 && code <= 209) {
          JsonNode body = response.getBody();
          JSONObject res = body.getObject();
          String serviceId = (String) res.get("id");
          if (serviceId != "") {
            createRoute(serviceId, client);
          }
        }
      });
  }

  public void updateService(ClientPayload client) {
    CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(KONG_URL + KONG_SERVICES)
      .header("accept", "application/json")
      .field("name", client.getTitle())
      .field("url", client.getHref())
      .asJsonAsync(response -> {
        int code = response.getStatus();
        if (code >= 200 && code <= 209) {
          JsonNode body = response.getBody();
          JSONObject res = body.getObject();
          String serviceId = (String) res.get("id");
          if (serviceId != "") {
            updateRoute(serviceId, client);
          }
        }
      });
  }

  public void createRoute(String serviceId, ClientPayload client) {
    if (client.isOidc()) {
      CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(KONG_URL+KONG_ROUTES)
        .header("accept", "application/json")
        .field("name", client.getTitle()+ "-" +KONG_ROUTES)
        .field("service.id", serviceId)
        .field("paths", client.getPath())
        .asJsonAsync(response -> {
          int code = response.getStatus();
          if (code >= 200 && code <= 209) {
            enableCORS(serviceId, client);
            enableOIDC(serviceId, client);
          }
        });
    }
  }

  public void updateRoute(String serviceId, ClientPayload client) {
    if (client.isOidc()) {
      CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(KONG_URL+KONG_ROUTES)
        .header("accept", "application/json")
        .field("name", client.getTitle()+ "-" +KONG_ROUTES)
        .field("service.id", serviceId)
        .field("paths", client.getPath())
        .asJsonAsync(response -> {
          int code = response.getStatus();
          enableOIDC(serviceId, client);
          enableCORS(serviceId, client);
        });
    }
  }

  public void enableCORS(String serviceId, ClientPayload client) {
    CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(KONG_URL
      +KONG_SERVICES + "/"
      + client.getTitle() + "/"
      +KONG_PLUGINS)
      .header("accept", "application/json")
      .field("name", KONG_CORS_PLUGIN)
      .field("config.preflight_continue", "false")
      .field("config.credentials", "true")
      .asJsonAsync(response -> {
        int code = response.getStatus();
      });
  }

  public void enableOIDC(String serviceId, ClientPayload client) {
    CompletableFuture<HttpResponse<JsonNode>> future = Unirest.post(KONG_URL
      +KONG_SERVICES + "/"
      + client.getTitle() + "/"
      +KONG_PLUGINS)
      .header("accept", "application/json")
      .field("name", KONG_OIDC_PLUGIN)
      .field("config.client_id", KONG_CONFIG_CLIENT_ID)
      .field("config.client_secret", KONG_CONFIG_CLIENT_SECRET)
      .field("config.discovery", KONG_CONFIG_DISCOVERY)
      .asJsonAsync(response -> {
        int code = response.getStatus();
      });
  }

  public JSONObject getKong(String type) {
    HttpResponse<JsonNode> response = Unirest.get(KONG_URL
      + type)
      .header("accept", "application/json")
      .asJson();

    int code = response.getStatus();
    if (code >= 200 && code <= 209) {
      JsonNode body = response.getBody();
      return body.getObject();
    }
    return null;
  }

  public void deleteService(ClientPayload client) {
    CompletableFuture<HttpResponse<JsonNode>> future = Unirest.delete(KONG_URL
      +KONG_ROUTES + "/"
      + client.getRouteId())
      .header("accept", "application/json")
      .asJsonAsync(response -> {
        int code = response.getStatus();
        if (code >= 200 && code <= 209) {
          Unirest.delete(KONG_URL
            +KONG_SERVICES + "/"
            + client.getServiceId())
            .header("accept", "application/json")
            .asJson();
        }
      });
  }

}
