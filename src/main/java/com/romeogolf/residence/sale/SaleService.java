package com.romeogolf.residence.sale;

import com.romeogolf.residence.sale.dto.SaleRequest;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.unit.Unit;
import com.romeogolf.residence.unit.UnitRepository;
import com.romeogolf.residence.unit.UnitStatus;
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

    public List<Sale> getAll() {
        return saleRepository.findAll();
    }

    public Sale getById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));
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
}
