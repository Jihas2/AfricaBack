package com.romeogolf.residence.unit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByStatus(UnitStatus status);
    boolean existsByRef(String ref);
    Optional<Unit> findByRef(String ref);
    long countByStatus(UnitStatus status);
}
