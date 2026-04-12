package com.romeogolf.residence.sale;

import com.romeogolf.residence.reservation.Reservation;
import com.romeogolf.residence.reservation.ReservationRepository;
import com.romeogolf.residence.sale.dto.SaleFromReservationRequest;
import com.romeogolf.residence.sale.dto.SaleRequest;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.unit.Unit;
import com.romeogolf.residence.unit.UnitRepository;
import com.romeogolf.residence.unit.UnitStatus;
import com.romeogolf.residence.unit.UnitType;
import com.romeogolf.residence.user.User;
import com.romeogolf.residence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final ReservationRepository reservationRepository;

    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    public Sale getById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Sale cancel(Long id) {
        Sale sale = getById(id);
        if (sale.getStatus() == SaleStatus.ANNULEE) {
            throw new ApiException("Cette vente est déjà annulée.", HttpStatus.BAD_REQUEST);
        }
        sale.setStatus(SaleStatus.ANNULEE);
        // Restore unit to available so it can be sold again
        Unit unit = sale.getUnit();
        unit.setStatus(UnitStatus.DISPONIBLE);
        unitRepository.save(unit);
        return saleRepository.save(sale);
    }

    @Transactional
    public Sale create(SaleRequest req) {
        User buyer = userRepository.findById(req.getBuyerId())
                .orElseThrow(() -> new ApiException("Acheteur introuvable.", HttpStatus.NOT_FOUND));

        Unit unit = unitRepository.findById(req.getUnitId())
                .orElseThrow(() -> new ApiException("Unité introuvable.", HttpStatus.NOT_FOUND));

        if (unit.getStatus() == UnitStatus.VENDUE) {
            throw new ApiException("Cette unité est déjà vendue.", HttpStatus.BAD_REQUEST);
        }

        Sale sale = Sale.builder()
                .buyer(buyer)
                .unit(unit)
                .totalAmount(req.getTotalAmount())
                .paidAmount(req.getPaidAmount())
                .notes(req.getNotes())
                .status(SaleStatus.EN_COURS)
                .build();

        unit.setStatus(UnitStatus.VENDUE);
        unitRepository.save(unit);

        return saleRepository.save(sale);
    }

    /**
     * Creates a sale directly from a reservation.
     * If no unit exists for the listing yet, one is created automatically
     * using the listing's type, floor, and price as a template.
     */
    @Transactional
    public Sale createFromReservation(SaleFromReservationRequest req) {
        Reservation reservation = reservationRepository.findById(req.getReservationId())
                .orElseThrow(() -> new ApiException("Réservation introuvable.", HttpStatus.NOT_FOUND));

        // Derive a stable unit ref from the listing ID so each listing gets exactly one unit
        String unitRef = "APT-" + reservation.getListing().getId();

        Unit unit = unitRepository.findByRef(unitRef).orElseGet(() -> {
            Unit newUnit = Unit.builder()
                    .ref(unitRef)
                    .type(UnitType.valueOf(reservation.getListing().getType().name()))
                    .floorNumber(reservation.getListing().getFloorNumber())
                    .price(reservation.getListing().getPrice())
                    .status(UnitStatus.DISPONIBLE)
                    .build();
            return unitRepository.save(newUnit);
        });

        if (unit.getStatus() == UnitStatus.VENDUE) {
            throw new ApiException("Cette unité est déjà vendue.", HttpStatus.BAD_REQUEST);
        }

        Sale sale = Sale.builder()
                .buyer(reservation.getUser())
                .unit(unit)
                .totalAmount(req.getTotalAmount())
                .paidAmount(java.math.BigDecimal.ZERO)
                .notes(req.getNotes())
                .status(SaleStatus.EN_COURS)
                .build();

        unit.setStatus(UnitStatus.VENDUE);
        unitRepository.save(unit);

        return saleRepository.save(sale);
    }
}
