package com.romeogolf.residence.checkout;

import com.romeogolf.residence.payment.Payment;
import com.romeogolf.residence.payment.PaymentMethod;
import com.romeogolf.residence.payment.PaymentRepository;
import com.romeogolf.residence.payment.PaymentService;
import com.romeogolf.residence.payment.PaymentStatus;
import com.romeogolf.residence.sale.Sale;
import com.romeogolf.residence.sale.SaleRepository;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalCheckoutService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final SaleRepository    saleRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService    paymentService;
    private final RestTemplate      restTemplate;

    private String getBaseUrl() {
        return "sandbox".equals(mode)
                ? "https://api-m.sandbox.paypal.com"
                : "https://api-m.paypal.com";
    }

    @SuppressWarnings("unchecked")
    private String getAccessToken() {
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/oauth2/token", entity, Map.class);
        return (String) response.getBody().get("access_token");
    }

    @SuppressWarnings("unchecked")
    public String createOrder(CheckoutRequest req, User user) {
        Sale sale = saleRepository.findById(req.getSaleId())
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));

        Payment payment = Payment.builder()
                .sale(sale)
                .amount(req.getAmount())
                .method(PaymentMethod.PAYPAL)
                .paymentDate(LocalDate.now())
                .status(PaymentStatus.EN_ATTENTE)
                .build();
        Payment saved = paymentRepository.save(payment);

        try {
            String token = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String amountStr = req.getAmount()
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString();

            Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                    "amount", Map.of(
                        "currency_code", req.getCurrency(),
                        "value", amountStr
                    ),
                    "description", "Acompte — Unité " + sale.getUnit().getRef() + " · Résidence Romeo Golf",
                    "custom_id", String.valueOf(saved.getId())
                )),
                "application_context", Map.of(
                    "return_url", frontendUrl + "/payment/success?provider=paypal&payment_id=" + saved.getId(),
                    "cancel_url", frontendUrl + "/payment/cancel?provider=paypal",
                    "brand_name", "Résidence Romeo Golf",
                    "landing_page", "NO_PREFERENCE",
                    "user_action", "PAY_NOW"
                )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v2/checkout/orders", entity, Map.class);

            List<Map<String, String>> links = (List<Map<String, String>>) response.getBody().get("links");
            return links.stream()
                    .filter(l -> "approve".equals(l.get("rel")))
                    .findFirst()
                    .map(l -> l.get("href"))
                    .orElseThrow(() -> new ApiException("URL PayPal d'approbation introuvable.", HttpStatus.INTERNAL_SERVER_ERROR));

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            paymentRepository.delete(saved);
            throw new ApiException("Erreur PayPal: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void captureOrder(String orderId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException("Paiement introuvable.", HttpStatus.NOT_FOUND));

        try {
            String token = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of(), headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture",
                    entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                paymentService.markReceived(payment.getId(), orderId);
            }
        } catch (Exception e) {
            throw new ApiException("Erreur de capture PayPal: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
