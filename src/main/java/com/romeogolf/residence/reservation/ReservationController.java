package com.romeogolf.residence.reservation;

import com.romeogolf.residence.reservation.dto.ReservationRequest;
import com.romeogolf.residence.reservation.dto.ReservationStatusRequest;
import com.romeogolf.residence.shared.ApiResponse;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService           reservationService;
    private final ReservationRepository        reservationRepository;
    private final ReservationMessageRepository messageRepository;

    // ─── User ─────────────────────────────────────────────────────────────────

    @PostMapping("/api/reservations")
    public ResponseEntity<ApiResponse<Reservation>> create(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User user) {
        Reservation r = reservationService.create(request, user);
        return ResponseEntity.status(201).body(ApiResponse.ok("Réservation créée.", r));
    }

    @GetMapping("/api/reservations/my")
    public ResponseEntity<ApiResponse<List<Reservation>>> getMy(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getMyReservations(user.getId())));
    }

    @DeleteMapping("/api/reservations/{id}")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id,
                                                    @AuthenticationPrincipal User user) {
        reservationService.cancel(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Réservation annulée.", null));
    }

    // ─── Buyer messages ───────────────────────────────────────────────────────

    @GetMapping("/api/reservations/{id}/messages")
    public ResponseEntity<ApiResponse<List<ReservationMessage>>> getBuyerMessages(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));
        if (!r.getUser().getId().equals(user.getId()))
            throw new ApiException("Accès refusé.", HttpStatus.FORBIDDEN);
        // mark admin messages as read (buyer is reading them)
        messageRepository.markAsRead(id, ReservationMessage.SenderType.ADMIN);
        return ResponseEntity.ok(ApiResponse.ok(
                messageRepository.findByReservationIdOrderByCreatedAtAsc(id)));
    }

    @PostMapping("/api/reservations/{id}/messages")
    public ResponseEntity<ApiResponse<ReservationMessage>> sendBuyerMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user) {
        String content = body.getOrDefault("content", "").trim();
        if (content.isEmpty()) throw new ApiException("Message vide.", HttpStatus.BAD_REQUEST);
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));
        if (!r.getUser().getId().equals(user.getId()))
            throw new ApiException("Accès refusé.", HttpStatus.FORBIDDEN);
        ReservationMessage msg = ReservationMessage.builder()
                .reservation(r)
                .senderType(ReservationMessage.SenderType.BUYER)
                .content(content)
                .build();
        return ResponseEntity.status(201).body(ApiResponse.ok(messageRepository.save(msg)));
    }

    // ─── Admin ────────────────────────────────────────────────────────────────

    @GetMapping("/api/admin/reservations")
    public ResponseEntity<ApiResponse<List<Reservation>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getAll()));
    }

    @PutMapping("/api/admin/reservations/{id}/status")
    public ResponseEntity<ApiResponse<Reservation>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour.", reservationService.updateStatus(id, request)));
    }

    @PutMapping("/api/admin/reservations/{id}/note")
    public ResponseEntity<ApiResponse<Reservation>> updateNote(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));
        r.setAdminNote(body.getOrDefault("note", "").trim());
        return ResponseEntity.ok(ApiResponse.ok("Note mise à jour.", reservationRepository.save(r)));
    }

    @GetMapping("/api/admin/reservations/{id}/messages")
    public ResponseEntity<ApiResponse<List<ReservationMessage>>> getAdminMessages(@PathVariable Long id) {
        reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));
        // mark buyer messages as read (admin is reading them)
        messageRepository.markAsRead(id, ReservationMessage.SenderType.BUYER);
        return ResponseEntity.ok(ApiResponse.ok(
                messageRepository.findByReservationIdOrderByCreatedAtAsc(id)));
    }

    @PostMapping("/api/admin/reservations/{id}/messages")
    public ResponseEntity<ApiResponse<ReservationMessage>> sendAdminMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String content = body.getOrDefault("content", "").trim();
        if (content.isEmpty()) throw new ApiException("Message vide.", HttpStatus.BAD_REQUEST);
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));
        ReservationMessage msg = ReservationMessage.builder()
                .reservation(r)
                .senderType(ReservationMessage.SenderType.ADMIN)
                .content(content)
                .build();
        return ResponseEntity.status(201).body(ApiResponse.ok(messageRepository.save(msg)));
    }

    @GetMapping("/api/admin/reservations/{id}/messages/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                messageRepository.countByReservationIdAndSenderTypeAndIsReadFalse(
                        id, ReservationMessage.SenderType.BUYER)));
    }
}
