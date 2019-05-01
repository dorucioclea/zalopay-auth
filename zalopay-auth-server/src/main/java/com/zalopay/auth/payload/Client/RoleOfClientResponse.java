package com.zalopay.auth.payload.Client;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleOfClientResponse {
    private String roleId;
    private String name;
    private String description;
}
