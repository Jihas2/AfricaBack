package com.romeogolf.residence.amenity.dto;

import jakarta.validation.constraints.NotBlank;

public record AmenityItemRequest(
    @NotBlank String url,
    String icon,
    @NotBlank String name,
    String caption,
    Integer sortOrder
) {}
