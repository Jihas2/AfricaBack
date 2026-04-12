package com.romeogolf.residence.reservation.dto;

import com.romeogolf.residence.reservation.ReservationMessage;

import java.time.LocalDateTime;

public record ReservationMessageDto(
        Long id,
        String senderType,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static ReservationMessageDto from(ReservationMessage msg) {
        return new ReservationMessageDto(
                msg.getId(),
                msg.getSenderType().name(),
                msg.getContent(),
                Boolean.TRUE.equals(msg.getIsRead()),
                msg.getCreatedAt()
        );
    }
}
