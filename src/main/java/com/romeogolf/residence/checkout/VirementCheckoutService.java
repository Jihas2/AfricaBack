package com.romeogolf.residence.checkout;

import com.romeogolf.residence.payment.Payment;
import com.romeogolf.residence.payment.PaymentMethod;
import com.romeogolf.residence.payment.PaymentRepository;
import com.romeogolf.residence.payment.PaymentStatus;
import com.romeogolf.residence.sale.Sale;
import com.romeogolf.residence.sale.SaleRepository;
import com.romeogolf.residence.shared.exception.ApiException;
import com.romeogolf.residence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VirementCheckoutService {

    @Value("${app.bank.account-name}")
    private String bankAccountName;

    @Value("${app.bank.account-number}")
    private String bankAccountNumber;

    @Value("${app.bank.bank-name}")
    private String bankName;

    @Value("${app.bank.swift:}")
    private String bankSwift;

    private final SaleRepository    saleRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Object> initiate(CheckoutRequest req, User user) {
        Sale sale = saleRepository.findById(req.getSaleId())
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));

        CheckoutValidator.validateOwnershipAndAmount(sale, user, req.getAmount());

        String reference = "RG-" + sale.getId() + "-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .sale(sale)
                .amount(req.getAmount())
                .method(PaymentMethod.VIREMENT)
                .paymentDate(LocalDate.now())
                .status(PaymentStatus.EN_ATTENTE)
                .bankAccount(bankAccountNumber)
                .reference(reference)
                .build();
        paymentRepository.save(payment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reference",         reference);
        result.put("bankAccountName",   bankAccountName);
        result.put("bankAccountNumber", bankAccountNumber);
        result.put("bankName",          bankName);
        result.put("swift",             bankSwift);
        result.put("amount",            req.getAmount());
        result.put("currency",          req.getCurrency());
        return result;
    }
}
