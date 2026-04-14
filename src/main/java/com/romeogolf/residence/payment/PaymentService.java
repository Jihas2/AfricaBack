package com.romeogolf.residence.payment;

import com.romeogolf.residence.payment.dto.PaymentRequest;
import com.romeogolf.residence.payment.dto.PaymentStatusRequest;
import com.romeogolf.residence.sale.Sale;
import com.romeogolf.residence.sale.SaleRepository;
import com.romeogolf.residence.sale.SaleStatus;
import com.romeogolf.residence.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository    saleRepository;

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Paiement introuvable.", HttpStatus.NOT_FOUND));
    }

    public List<Payment> getBySale(Long saleId) {
        return paymentRepository.findBySaleId(saleId);
    }

    @Transactional
    public Payment create(PaymentRequest req) {
        Sale sale = saleRepository.findById(req.getSaleId())
                .orElseThrow(() -> new ApiException("Vente introuvable.", HttpStatus.NOT_FOUND));

        Payment payment = Payment.builder()
                .sale(sale)
                .amount(req.getAmount())
                .method(req.getMethod())
                .paymentDate(req.getPaymentDate())
                .reference(req.getReference())
                .notes(req.getNotes())
                .status(PaymentStatus.EN_ATTENTE)
                .build();

        Payment saved = paymentRepository.save(payment);

        // Update paidAmount on the sale
        recalculateSalePaidAmount(sale);

        return saved;
    }

    @Transactional
    public Payment updateStatus(Long id, PaymentStatusRequest req) {
        Payment payment = getById(id);
        payment.setStatus(req.getStatus());

        if (req.getStatus() == PaymentStatus.DEPOSE) {
            if (req.getBankAccount() != null) payment.setBankAccount(req.getBankAccount());
            payment.setDepositedAt(req.getDepositedAt() != null ? req.getDepositedAt() : LocalDate.now());
        }

        if (req.getNotes() != null) payment.setNotes(req.getNotes());

        Payment saved = paymentRepository.save(payment);

        // Recalculate sale paid amount and status
        recalculateSalePaidAmount(payment.getSale());

        return saved;
    }

    @Transactional
    public void markReceived(Long paymentId, String reference) {
        Payment payment = getById(paymentId);
        // Idempotency guard: webhook retries must not re-process an already confirmed payment.
        if (payment.getStatus() == PaymentStatus.RECU || payment.getStatus() == PaymentStatus.DEPOSE) {
            return;
        }
        payment.setStatus(PaymentStatus.RECU);
        if (reference != null) payment.setReference(reference);
        paymentRepository.save(payment);
        recalculateSalePaidAmount(payment.getSale());
    }

    private void recalculateSalePaidAmount(Sale sale) {
        BigDecimal totalPaid = paymentRepository.findBySaleId(sale.getId()).stream()
                .filter(p -> p.getStatus() == PaymentStatus.DEPOSE || p.getStatus() == PaymentStatus.RECU)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sale.setPaidAmount(totalPaid);

        if (totalPaid.compareTo(sale.getTotalAmount()) >= 0) {
            sale.setStatus(SaleStatus.COMPLETE);
        }

        saleRepository.save(sale);
    }
}
