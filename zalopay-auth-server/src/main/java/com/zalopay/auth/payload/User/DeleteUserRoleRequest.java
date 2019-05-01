package com.zalopay.auth.payload.User;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRoleRequest {

    @NotBlank
    private String clientId;

    @NotBlank
    private String userId;

    @NotBlank
        private String userName;

    @NotBlank
    private String roles;
}
