package com.romeogolf.residence.checkout;

import com.romeogolf.residence.shared.ApiResponse;
import com.romeogolf.residence.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CheckoutController {

    private final StripeCheckoutService  stripe;
    private final PaypalCheckoutService  paypal;
    private final CinetPayCheckoutService cinetpay;

    // ─── Initiation (authenticated buyer) ────────────────────────────────────

    @PostMapping("/api/checkout/stripe")
    public ResponseEntity<ApiResponse<Map<String, String>>> stripeInit(
            @Valid @RequestBody CheckoutRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", stripe.createSession(req, user))));
    }

    @PostMapping("/api/checkout/paypal")
    public ResponseEntity<ApiResponse<Map<String, String>>> paypalInit(
            @Valid @RequestBody CheckoutRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", paypal.createOrder(req, user))));
    }

    @PostMapping("/api/checkout/cinetpay")
    public ResponseEntity<ApiResponse<Map<String, String>>> cinetpayInit(
            @Valid @RequestBody CheckoutRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", cinetpay.initPayment(req, user))));
    }

    // ─── PayPal capture (called by frontend after approval redirect) ──────────

    @PostMapping("/api/checkout/paypal/capture")
    public ResponseEntity<ApiResponse<Void>> paypalCapture(
            @RequestParam String token,
            @RequestParam Long paymentId) {
        paypal.captureOrder(token, paymentId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ─── Webhooks (no auth — called by gateway servers) ──────────────────────

    @PostMapping("/api/webhooks/stripe")
    public ResponseEntity<Void> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sig) {
        stripe.handleWebhook(payload, sig);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/webhooks/cinetpay")
    public ResponseEntity<Void> cinetpayNotify(@RequestBody Map<String, String> body) {
        cinetpay.handleNotification(body);
        return ResponseEntity.ok().build();
    }
}
