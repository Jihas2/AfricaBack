package com.romeogolf.residence.checkout;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckoutRequest {

    @NotNull(message = "L'identifiant de la vente est obligatoire.")
    private Long saleId;

    @NotNull(message = "Le montant est obligatoire.")
    @Positive(message = "Le montant doit être positif.")
    private BigDecimal amount;

    private String currency = "USD";
}
