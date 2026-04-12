package com.romeogolf.residence.reservation.dto;

import com.romeogolf.residence.reservation.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationStatusRequest {

    @NotNull(message = "Le statut est obligatoire.")
    private ReservationStatus status;
}
