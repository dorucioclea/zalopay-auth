package com.zalopay.auth.payload.Client;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRoleRequest {
    private String clientId;
    private String name;
    private String description;
}
