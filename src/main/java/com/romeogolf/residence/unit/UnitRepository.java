package com.romeogolf.residence.unit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByStatus(UnitStatus status);
    boolean existsByRef(String ref);
    long countByStatus(UnitStatus status);
}
