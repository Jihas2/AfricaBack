package com.romeogolf.residence.checkout;

import com.romeogolf.residence.sale.Sale;
import com.romeogolf.residence.sale.SaleStatus;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.user.User;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

/**
 * Shared validation logic for all checkout providers.
 */
final class CheckoutValidator {

    private CheckoutValidator() {}

    /**
     * Verifies that the authenticated user owns the sale and that
     * the requested amount is within the remaining balance.
     *
     * @throws ApiException FORBIDDEN if the user is not the buyer of the sale.
     * @throws ApiException BAD_REQUEST if the amount exceeds the remaining balance or the sale is complete.
     */
    static void validateOwnershipAndAmount(Sale sale, User user, BigDecimal amount) {
        if (!sale.getBuyer().getId().equals(user.getId())) {
            throw new ApiException("Accès refusé : cette vente ne vous appartient pas.", HttpStatus.FORBIDDEN);
        }

        if (sale.getStatus() == SaleStatus.COMPLETE) {
            throw new ApiException("Cette vente est déjà entièrement réglée.", HttpStatus.BAD_REQUEST);
        }

        BigDecimal remaining = sale.getTotalAmount().subtract(sale.getPaidAmount());
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Aucun solde restant pour cette vente.", HttpStatus.BAD_REQUEST);
        }

        if (amount.compareTo(remaining) > 0) {
            throw new ApiException(
                "Le montant (" + amount.toPlainString() + ") dépasse le solde restant (" + remaining.toPlainString() + ").",
                HttpStatus.BAD_REQUEST
            );
        }
    }
}
