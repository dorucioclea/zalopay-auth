package com.zalopay.auth.payload.User;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesForClientRequest {
    private String clientId;
    private String userId;
    private List<String> roles;
}
