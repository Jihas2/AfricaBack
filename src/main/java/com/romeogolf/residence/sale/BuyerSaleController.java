package com.romeogolf.residence.sale;

import com.romeogolf.residence.shared.ApiResponse;
import com.romeogolf.residence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BuyerSaleController {

    private final SaleRepository saleRepository;

    // GET /api/my-sales — returns all sales for the authenticated buyer
    @GetMapping("/api/my-sales")
    public ResponseEntity<ApiResponse<List<Sale>>> getMySales(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(saleRepository.findByBuyerId(user.getId())));
    }
}
