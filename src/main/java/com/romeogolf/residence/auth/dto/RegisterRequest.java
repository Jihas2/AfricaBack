package com.romeogolf.residence.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message   = "Format d'email invalide.")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire.")
    private String phone;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
    private String password;
}
