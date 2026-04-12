package com.romeogolf.residence.payment.dto;

import com.romeogolf.residence.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequest {

    @NotNull(message = "L'identifiant de la vente est obligatoire.")
    private Long saleId;

    @NotNull(message = "Le montant est obligatoire.")
    @Positive(message = "Le montant doit être positif.")
    private BigDecimal amount;

    @NotNull(message = "La méthode de paiement est obligatoire.")
    private PaymentMethod method;

    @NotNull(message = "La date de paiement est obligatoire.")
    private LocalDate paymentDate;

    private String reference;
    private String notes;
}
