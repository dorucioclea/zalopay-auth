package com.zalopay.auth.security;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KeyCloakConnectionProvider {

  private static Keycloak keycloak;

  private static final Logger logger = LoggerFactory.getLogger(KeyCloakConnectionProvider.class);

  private static String SSO_URL = "http://localhost:8180/auth";

  private static String SSO_REALM ="zalorealm";

  private static String SSO_CLIENT_ID = "kong";

  private static String SSO_ACCOUNT = "soaccount";

  private static String SSO_PASS = "123account";


//  private static String SSO_URL;
//
//  private static String SSO_REALM;
//
//  private static String SSO_CLIENT_ID;
//
//  private static String SSO_ACCOUNT;
//
//  private static String SSO_PASS;

  static {
    try {
      initialiseConnection();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    registerShutDownHook();
  }

  /**
   * Method to initializate the Keycloak connection
   * @return Keycloak connection
   */
  public static Keycloak initialiseConnection() {
    logger.info("key cloak instance is creation started.");
    keycloak = initialiseEnvConnection();
    if (keycloak != null) {
      return keycloak;
    }

    KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder()
      .serverUrl(SSO_URL)
      .realm(SSO_REALM)
      .username(SSO_ACCOUNT)
      .password(SSO_PASS)
      .clientId(SSO_CLIENT_ID);

    keycloak = keycloakBuilder.build();

    logger.info("key cloak instance is created successfully.");
    return keycloak;
  }

  /**
   * This method will provide the keycloak connection from
   * environment variable. if environment variable is not set
   * then it will return null.
   * @return Keycloak
   */
  private static Keycloak initialiseEnvConnection() {

    KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder()
      .serverUrl(SSO_URL)
      .realm(SSO_REALM)
      .username(SSO_ACCOUNT)
      .password(SSO_PASS)
      .clientId(SSO_CLIENT_ID);

    keycloak = keycloakBuilder.build();
    logger.info("key cloak instance is created from Environment variable settings .");
    return keycloak;
  }


  /**
   * This method will provide key cloak
   * connection instance.
   * @return Keycloak
   */
  public static Keycloak getConnection() {
    if (keycloak != null) {
      return keycloak;
    } else {
      try {
        return initialiseConnection();
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
    return null;
  }

  /**
   * This class will be called by registerShutDownHook to
   * register the call inside jvm , when jvm terminate it will call
   * the run method to clean up the resource.
   *
   */
  static class ResourceCleanUp extends Thread {
    public void run() {
      keycloak.close();
    }
  }

  /**
   * Register the hook for resource clean up.
   * this will be called when jvm shut down.
   */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
    logger.info("ShutDownHook registered.");
  }

  /*
  @Value("${keycloak.auth-server-url}")
  public void setSSO_URL(String SSO_URL) {
    this.SSO_URL = SSO_URL;
  }

  @Value("${keycloak.realm}")
  public void setSSO_REALM(String SSO_REALM) {
    this.SSO_REALM = SSO_REALM;
  }

  @Value("${keycloak.resource}")
  public void setSSO_CLIENT_ID(String SSO_CLIENT_ID) {
    this.SSO_CLIENT_ID = SSO_CLIENT_ID;
  }

  @Value("${zaloAuth.account}")
  public void setSSO_ACCOUNT(String SSO_ACCOUNT) {
    this.SSO_ACCOUNT = SSO_ACCOUNT;
  }

  @Value("${zaloAuth.password}")
  public void setSSO_PASS(String SSO_PASS) {
    this.SSO_PASS = SSO_PASS;
  }*/
}
