package com.romeogolf.residence.reservation;

import com.romeogolf.residence.listing.Listing;
import com.romeogolf.residence.listing.ListingRepository;
import com.romeogolf.residence.listing.ListingStatus;
import com.romeogolf.residence.reservation.dto.ReservationRequest;
import com.romeogolf.residence.reservation.dto.ReservationStatusRequest;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ListingRepository     listingRepository;

    // ─── User ─────────────────────────────────────────────────────────────────
    public Reservation create(ReservationRequest req, User user) {
        Listing listing = listingRepository.findById(req.getListingId())
                .orElseThrow(() -> new ApiException("Annonce introuvable.", HttpStatus.NOT_FOUND));

        if (listing.getStatus() != ListingStatus.PUBLIE) {
            throw new ApiException("Cette annonce n'est plus disponible.", HttpStatus.BAD_REQUEST);
        }

        Reservation reservation = Reservation.builder()
                .listing(listing)
                .user(user)
                .message(req.getMessage())
                .status(ReservationStatus.EN_ATTENTE)
                .build();

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getMyReservations(Long userId) {
        return reservationRepository.findByUserIdAndStatusNot(userId, ReservationStatus.ANNULEE);
    }

    public void cancel(Long id, User user) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ApiException("Accès refusé.", HttpStatus.FORBIDDEN);
        }

        reservation.setStatus(ReservationStatus.ANNULEE);
        reservationRepository.save(reservation);
    }

    // ─── Admin ────────────────────────────────────────────────────────────────
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Reservation updateStatus(Long id, ReservationStatusRequest req) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));

        reservation.setStatus(req.getStatus());
        return reservationRepository.save(reservation);
    }
}
