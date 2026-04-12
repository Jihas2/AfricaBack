package com.romeogolf.residence.sale.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleFromReservationRequest {

    @NotNull
    private Long reservationId;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    private String notes;
}
