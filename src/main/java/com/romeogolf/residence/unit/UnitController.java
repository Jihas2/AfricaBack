package com.romeogolf.residence.unit;

import com.romeogolf.residence.shared.ApiResponse;
import com.romeogolf.residence.unit.dto.UnitRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Unit>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Unit>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(unitService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Unit>> create(@Valid @RequestBody UnitRequest request) {
        Unit unit = unitService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.ok("Unité créée.", unit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Unit>> update(@PathVariable Long id,
                                                    @Valid @RequestBody UnitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Unité mise à jour.", unitService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        unitService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Unité supprimée.", null));
    }
}
