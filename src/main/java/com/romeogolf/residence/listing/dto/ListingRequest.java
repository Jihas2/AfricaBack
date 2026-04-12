package com.romeogolf.residence.listing.dto;

import com.romeogolf.residence.listing.ListingStatus;
import com.romeogolf.residence.listing.ListingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ListingRequest {

    @NotBlank(message = "Le titre est obligatoire.")
    private String title;

    private String description;

    @NotNull(message = "Le type est obligatoire.")
    private ListingType type;

    @NotNull(message = "Le prix est obligatoire.")
    @Positive(message = "Le prix doit être positif.")
    private BigDecimal price;

    @NotNull(message = "La surface est obligatoire.")
    @Positive(message = "La surface doit être positive.")
    private Double surface;

    @NotNull(message = "L'étage est obligatoire.")
    private Integer floorNumber;

    @NotNull(message = "Le nombre de chambres est obligatoire.")
    private Integer bedrooms;

    @NotNull(message = "Le nombre de salles de bain est obligatoire.")
    private Integer bathrooms;

    private List<String> features = new ArrayList<>();
    private List<String> photos   = new ArrayList<>();

    private ListingStatus status = ListingStatus.BROUILLON;
}
