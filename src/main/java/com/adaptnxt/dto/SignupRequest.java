package com.adaptnxt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    // Optional role: either "ROLE_CUSTOMER" or "ROLE_ADMIN"
    private String role = "ROLE_CUSTOMER";
}
