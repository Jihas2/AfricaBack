package com.romeogolf.residence.amenity;

import com.romeogolf.residence.amenity.dto.AmenityItemRequest;
import com.romeogolf.residence.shared.ApiResponse;
import com.romeogolf.residence.shared.exception.ApiException;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityItemRepository repo;

    // ─── Public ──────────────────────────────────────────────────────────────
    @GetMapping("/api/amenities")
    public ResponseEntity<ApiResponse<List<AmenityItem>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(repo.findAllByOrderBySortOrderAsc()));
    }

    // ─── Admin ───────────────────────────────────────────────────────────────
    @PostMapping("/api/admin/amenities")
    public ResponseEntity<ApiResponse<AmenityItem>> create(@Valid @RequestBody AmenityItemRequest req) {
        AmenityItem item = AmenityItem.builder()
            .url(req.url())
            .icon(req.icon())
            .name(req.name())
            .caption(req.caption())
            .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
            .build();
        return ResponseEntity.status(201).body(ApiResponse.ok("Commodité ajoutée.", repo.save(item)));
    }

    @PutMapping("/api/admin/amenities/{id}")
    public ResponseEntity<ApiResponse<AmenityItem>> update(@PathVariable Long id,
                                                           @Valid @RequestBody AmenityItemRequest req) {
        AmenityItem item = repo.findById(id).orElseThrow(() -> new ApiException("Commodité introuvable.", HttpStatus.NOT_FOUND));
        item.setUrl(req.url());
        item.setIcon(req.icon());
        item.setName(req.name());
        item.setCaption(req.caption());
        if (req.sortOrder() != null) item.setSortOrder(req.sortOrder());
        return ResponseEntity.ok(ApiResponse.ok("Commodité mise à jour.", repo.save(item)));
    }

    @DeleteMapping("/api/admin/amenities/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        repo.findById(id).orElseThrow(() -> new ApiException("Commodité introuvable.", HttpStatus.NOT_FOUND));
        repo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Commodité supprimée.", null));
    }
}
