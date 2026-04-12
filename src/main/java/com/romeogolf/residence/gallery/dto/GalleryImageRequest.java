package com.romeogolf.residence.gallery.dto;

import jakarta.validation.constraints.NotBlank;

public record GalleryImageRequest(
    @NotBlank String url,
    String caption,
    Integer sortOrder
) {}
