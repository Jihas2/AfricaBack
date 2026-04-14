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
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StripeCheckoutService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final SaleRepository    saleRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService    paymentService;

    public String createSession(CheckoutRequest req, User user) {
        Stripe.apiKey = secretKey;

        Sale sale = saleRepository.findById(req.getSaleId())
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));

        CheckoutValidator.validateOwnershipAndAmount(sale, user, req.getAmount());

        Payment payment = Payment.builder()
                .sale(sale)
                .amount(req.getAmount())
                .method(PaymentMethod.STRIPE)
                .paymentDate(LocalDate.now())
                .status(PaymentStatus.EN_ATTENTE)
                .build();
        Payment saved = paymentRepository.save(payment);

        try {
            long amountCents = req.getAmount()
                    .multiply(new BigDecimal("100"))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?provider=stripe&session_id={CHECKOUT_SESSION_ID}&payment_id=" + saved.getId())
                    .setCancelUrl(frontendUrl + "/payment/cancel?provider=stripe")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(req.getCurrency().toLowerCase())
                                    .setUnitAmount(amountCents)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Acompte — Unité " + sale.getUnit().getRef())
                                            .setDescription("Résidence Romeo Golf · " + sale.getUnit().getType())
                                            .build())
                                    .build())
                            .build())
                    .putMetadata("payment_id", String.valueOf(saved.getId()))
                    .putMetadata("sale_id", String.valueOf(sale.getId()))
                    .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (StripeException e) {
            paymentRepository.delete(saved);
            throw new ApiException("Erreur Stripe: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void handleWebhook(byte[] rawPayload, String sigHeader) {
        Stripe.apiKey = secretKey;
        // Use the raw bytes decoded as UTF-8 to guarantee HMAC-SHA256 signature integrity.
        String payload = new String(rawPayload, StandardCharsets.UTF_8);
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            if ("checkout.session.completed".equals(event.getType())) {
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                if (deserializer.getObject().isPresent()) {
                    Session session = (Session) deserializer.getObject().get();
                    String paymentIdStr = session.getMetadata().get("payment_id");
                    if (paymentIdStr != null) {
                        paymentService.markReceived(Long.parseLong(paymentIdStr), session.getId());
                    }
                }
            }
        } catch (SignatureVerificationException e) {
            throw new ApiException("Signature Stripe invalide.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new ApiException("Erreur webhook: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
