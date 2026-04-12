package com.romeogolf.residence.sale.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleRequest {

    @NotNull(message = "L'identifiant de l'acheteur est obligatoire.")
    private Long buyerId;

    @NotNull(message = "L'identifiant de l'unité est obligatoire.")
    private Long unitId;

    @NotNull(message = "Le montant total est obligatoire.")
    @Positive(message = "Le montant total doit être positif.")
    private BigDecimal totalAmount;

    private BigDecimal paidAmount = BigDecimal.ZERO;

    private String notes;
}
