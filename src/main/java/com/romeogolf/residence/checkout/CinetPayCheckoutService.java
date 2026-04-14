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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinetPayCheckoutService {

    @Value("${cinetpay.api-key}")
    private String apiKey;

    @Value("${cinetpay.site-id}")
    private String siteId;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    private final SaleRepository    saleRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService    paymentService;
    private final RestTemplate      restTemplate;

    @SuppressWarnings("unchecked")
    public String initPayment(CheckoutRequest req, User user) {
        Sale sale = saleRepository.findById(req.getSaleId())
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));

        CheckoutValidator.validateOwnershipAndAmount(sale, user, req.getAmount());

        Payment payment = Payment.builder()
                .sale(sale)
                .amount(req.getAmount())
                .method(PaymentMethod.CINETPAY)
                .paymentDate(LocalDate.now())
                .status(PaymentStatus.EN_ATTENTE)
                .build();
        Payment saved = paymentRepository.save(payment);

        // Unique transaction ID for CinetPay
        String transactionId = "RG-" + saved.getId() + "-" + System.currentTimeMillis();
        saved.setReference(transactionId);
        paymentRepository.save(saved);

        Map<String, Object> body = new HashMap<>();
        body.put("apikey", apiKey);
        body.put("site_id", siteId);
        body.put("transaction_id", transactionId);
        body.put("amount", req.getAmount().intValue());
        body.put("currency", req.getCurrency());
        body.put("description", "Acompte — Unité " + sale.getUnit().getRef() + " · Résidence Romeo Golf");
        body.put("return_url", frontendUrl + "/payment/success?provider=cinetpay&payment_id=" + saved.getId());
        body.put("notify_url", backendUrl + "/api/webhooks/cinetpay");
        body.put("channels", "ALL");
        body.put("lang", "fr");
        body.put("metadata", String.valueOf(saved.getId()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api-checkout.cinetpay.com/v2/payment", entity, Map.class);

            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            if (data == null) {
                throw new ApiException("Réponse CinetPay invalide.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String paymentUrl = (String) data.get("payment_url");
            if (paymentUrl == null) {
                throw new ApiException("URL CinetPay introuvable.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return paymentUrl;

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            paymentRepository.delete(saved);
            throw new ApiException("Erreur CinetPay: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void handleNotification(Map<String, String> body) {
        String transactionId = body.get("cpm_trans_id");
        if (transactionId == null) return;

        paymentRepository.findByReference(transactionId).ifPresent(payment -> {
            try {
                Map<String, Object> checkBody = new HashMap<>();
                checkBody.put("apikey", apiKey);
                checkBody.put("site_id", siteId);
                checkBody.put("transaction_id", transactionId);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(checkBody, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                        "https://api-checkout.cinetpay.com/v2/payment/check", entity, Map.class);

                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null && "ACCEPTED".equals(data.get("status"))) {
                    paymentService.markReceived(payment.getId(), transactionId);
                }
            } catch (Exception e) {
                // CinetPay will retry the IPN notification — log the error for monitoring
                log.error("CinetPay IPN processing failed for transaction {}: {}", transactionId, e.getMessage(), e);
            }
        });
    }
}
