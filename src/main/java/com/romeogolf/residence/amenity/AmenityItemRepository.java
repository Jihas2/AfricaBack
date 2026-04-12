package com.romeogolf.residence.amenity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmenityItemRepository extends JpaRepository<AmenityItem, Long> {
    List<AmenityItem> findAllByOrderBySortOrderAsc();
}
