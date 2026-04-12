package com.romeogolf.residence.payment.dto;

import com.romeogolf.residence.payment.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentStatusRequest {

    @NotNull(message = "Le statut est obligatoire.")
    private PaymentStatus status;

    private String    bankAccount;
    private LocalDate depositedAt;
    private String    notes;
}
