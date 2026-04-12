package com.romeogolf.residence.sale;

import com.romeogolf.residence.sale.dto.SaleFromReservationRequest;
import com.romeogolf.residence.sale.dto.SaleRequest;
import com.romeogolf.residence.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService    saleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sale>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sale>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Sale>> create(@Valid @RequestBody SaleRequest request) {
        Sale sale = saleService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.ok("Vente créée.", sale));
    }

    @PostMapping("/from-reservation")
    public ResponseEntity<ApiResponse<Sale>> createFromReservation(@Valid @RequestBody SaleFromReservationRequest request) {
        Sale sale = saleService.createFromReservation(request);
        return ResponseEntity.status(201).body(ApiResponse.ok("Vente créée.", sale));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Sale>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Vente annulée.", saleService.cancel(id)));
    }
}
