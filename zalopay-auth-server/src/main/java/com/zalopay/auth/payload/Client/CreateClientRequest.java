package com.zalopay.auth.payload.Client;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {
    private String clientName;
    private String clientRedirectURL;
}
