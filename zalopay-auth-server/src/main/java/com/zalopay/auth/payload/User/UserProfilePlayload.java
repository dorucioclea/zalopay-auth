package com.zalopay.auth.payload.User;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePlayload {
    private String realmId;
    private String username;
    private String name;
    private String email;
    private String roles;
    private int status;
}
