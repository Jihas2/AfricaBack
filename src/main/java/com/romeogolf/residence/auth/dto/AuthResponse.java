package com.romeogolf.residence.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long   id;
    private String name;
    private String email;
    private String phone;
    private String role;
}
