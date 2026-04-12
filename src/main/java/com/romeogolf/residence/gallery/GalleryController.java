package com.romeogolf.residence.gallery;

import com.romeogolf.residence.gallery.dto.GalleryImageRequest;
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
public class GalleryController {

    private final GalleryImageRepository repo;

    // ─── Public ──────────────────────────────────────────────────────────────
    @GetMapping("/api/gallery")
    public ResponseEntity<ApiResponse<List<GalleryImage>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(repo.findAllByOrderBySortOrderAsc()));
    }

    // ─── Admin ───────────────────────────────────────────────────────────────
    @PostMapping("/api/admin/gallery")
    public ResponseEntity<ApiResponse<GalleryImage>> create(@Valid @RequestBody GalleryImageRequest req) {
        GalleryImage img = GalleryImage.builder()
            .url(req.url())
            .caption(req.caption())
            .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
            .build();
        return ResponseEntity.status(201).body(ApiResponse.ok("Image ajoutée.", repo.save(img)));
    }

    @DeleteMapping("/api/admin/gallery/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        repo.findById(id).orElseThrow(() -> new ApiException("Image introuvable.", HttpStatus.NOT_FOUND));
        repo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Image supprimée.", null));
    }
}
