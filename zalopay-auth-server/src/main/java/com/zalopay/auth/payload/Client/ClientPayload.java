package com.zalopay.auth.payload.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientPayload {

  private String id;
  private List<String> roles;
  private String title;
  private String avatar;
  private int cover;
  private String status;
  private int percent;
  private String logo;
  private String href;
  private Date updatedAt;
  private Date createdAt;
  private String subDescription;
  private String description;
  private int activeUser;
  private String newUser;
  private int star;
  private int like;
  private int message;
  private String content;
  private ArrayList<String> members;
  private String path;
  private String routeId;
  private String serviceId;
  private boolean oidc;
  private String method;

  public List<String> getRoles() {
    List<String> rolesTrim = new ArrayList<>();
    for(String role: roles) {
      rolesTrim.add(role.trim());
    }
    return rolesTrim;
  }
}
