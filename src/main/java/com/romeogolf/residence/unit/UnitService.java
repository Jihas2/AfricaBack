package com.romeogolf.residence.unit;

import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.unit.dto.UnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public List<Unit> getAll() {
        return unitRepository.findAll();
    }

    public Unit getById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new ApiException("Unité introuvable.", HttpStatus.NOT_FOUND));
    }

    public Unit create(UnitRequest req) {
        if (unitRepository.existsByRef(req.getRef())) {
            throw new ApiException("La référence '" + req.getRef() + "' existe déjà.", HttpStatus.CONFLICT);
        }
        Unit unit = Unit.builder()
                .ref(req.getRef())
                .type(req.getType())
                .floorNumber(req.getFloorNumber())
                .price(req.getPrice())
                .status(req.getStatus())
                .build();
        return unitRepository.save(unit);
    }

    public Unit update(Long id, UnitRequest req) {
        Unit unit = getById(id);
        unit.setRef(req.getRef());
        unit.setType(req.getType());
        unit.setFloorNumber(req.getFloorNumber());
        unit.setPrice(req.getPrice());
        unit.setStatus(req.getStatus());
        return unitRepository.save(unit);
    }

    public void delete(Long id) {
        if (!unitRepository.existsById(id)) {
            throw new ApiException("Unité introuvable.", HttpStatus.NOT_FOUND);
        }
        unitRepository.deleteById(id);
    }
}
