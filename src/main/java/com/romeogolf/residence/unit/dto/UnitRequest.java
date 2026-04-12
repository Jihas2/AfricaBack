package com.romeogolf.residence.unit.dto;

import com.romeogolf.residence.unit.UnitStatus;
import com.romeogolf.residence.unit.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UnitRequest {

    @NotBlank(message = "La référence est obligatoire.")
    private String ref;

    @NotNull(message = "Le type est obligatoire.")
    private UnitType type;

    @NotNull(message = "L'étage est obligatoire.")
    private Integer floorNumber;

    @NotNull(message = "Le prix est obligatoire.")
    @Positive(message = "Le prix doit être positif.")
    private BigDecimal price;

    private UnitStatus status = UnitStatus.DISPONIBLE;
}
