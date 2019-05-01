package com.zalopay.auth.payload;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}
