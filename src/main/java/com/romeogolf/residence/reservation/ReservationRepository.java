package com.romeogolf.residence.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByUserIdAndStatusNot(Long userId, ReservationStatus status);
    long countByStatus(ReservationStatus status);
}
