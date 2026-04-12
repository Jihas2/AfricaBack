package com.romeogolf.residence.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReservationMessageRepository extends JpaRepository<ReservationMessage, Long> {

    List<ReservationMessage> findByReservationIdOrderByCreatedAtAsc(Long reservationId);

    long countByReservationIdAndSenderTypeAndIsReadFalse(Long reservationId, ReservationMessage.SenderType senderType);

    @Modifying
    @Transactional
    @Query("UPDATE ReservationMessage m SET m.isRead = true WHERE m.reservation.id = :reservationId AND m.senderType = :senderType AND m.isRead = false")
    void markAsRead(Long reservationId, ReservationMessage.SenderType senderType);
}
