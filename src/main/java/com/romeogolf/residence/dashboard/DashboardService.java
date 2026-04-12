package com.romeogolf.residence.dashboard;

import com.romeogolf.residence.dashboard.dto.DashboardStats;
import com.romeogolf.residence.payment.PaymentRepository;
import com.romeogolf.residence.payment.PaymentStatus;
import com.romeogolf.residence.sale.SaleRepository;
import com.romeogolf.residence.sale.SaleStatus;
import com.romeogolf.residence.unit.UnitRepository;
import com.romeogolf.residence.unit.UnitStatus;
import com.romeogolf.residence.user.UserRepository;
import com.romeogolf.residence.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository    saleRepository;
    private final PaymentRepository paymentRepository;
    private final UnitRepository    unitRepository;
    private final UserRepository    userRepository;

    public DashboardStats getStats() {
        return new DashboardStats(
                saleRepository.sumPaidAmount(),
                saleRepository.count(),
                saleRepository.countByStatus(SaleStatus.COMPLETE),
                paymentRepository.countByStatus(PaymentStatus.EN_ATTENTE),
                unitRepository.countByStatus(UnitStatus.DISPONIBLE),
                userRepository.countByRole(UserRole.USER)
        );
    }
}
