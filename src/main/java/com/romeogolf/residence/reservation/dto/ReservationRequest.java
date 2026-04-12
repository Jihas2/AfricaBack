package com.romeogolf.residence.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotNull(message = "L'identifiant de l'annonce est obligatoire.")
    private Long listingId;

    private String message;
}
