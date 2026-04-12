package com.romeogolf.residence.payment;

import com.romeogolf.residence.payment.dto.PaymentRequest;
import com.romeogolf.residence.payment.dto.PaymentStatusRequest;
import com.romeogolf.residence.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getById(id)));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getBySale(@PathVariable Long saleId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getBySale(saleId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Payment>> create(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.ok("Paiement créé.", payment));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Payment>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour.", paymentService.updateStatus(id, request)));
    }
}
