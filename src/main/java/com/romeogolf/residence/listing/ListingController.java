package com.romeogolf.residence.listing;

import com.romeogolf.residence.listing.dto.ListingRequest;
import com.romeogolf.residence.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    // ─── Public ──────────────────────────────────────────────────────────────
    // GET /api/listings
    @GetMapping("/api/listings")
    public ResponseEntity<ApiResponse<List<Listing>>> getPublished() {
        return ResponseEntity.ok(ApiResponse.ok(listingService.getPublished()));
    }

    // ─── Admin ───────────────────────────────────────────────────────────────
    // GET /api/admin/listings
    @GetMapping("/api/admin/listings")
    public ResponseEntity<ApiResponse<List<Listing>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(listingService.getAll()));
    }

    // GET /api/admin/listings/{id}
    @GetMapping("/api/admin/listings/{id}")
    public ResponseEntity<ApiResponse<Listing>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(listingService.getById(id)));
    }

    // POST /api/admin/listings
    @PostMapping("/api/admin/listings")
    public ResponseEntity<ApiResponse<Listing>> create(@Valid @RequestBody ListingRequest request) {
        Listing created = listingService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.ok("Annonce créée.", created));
    }

    // PUT /api/admin/listings/{id}
    @PutMapping("/api/admin/listings/{id}")
    public ResponseEntity<ApiResponse<Listing>> update(@PathVariable Long id,
                                                       @Valid @RequestBody ListingRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Annonce mise à jour.", listingService.update(id, request)));
    }

    // DELETE /api/admin/listings/{id}
    @DeleteMapping("/api/admin/listings/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        listingService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Annonce supprimée.", null));
    }
}
