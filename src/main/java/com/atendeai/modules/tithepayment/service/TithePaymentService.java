package com.atendeai.modules.tithepayment.service;

import com.atendeai.modules.auth.repository.UserRepository;
import com.atendeai.modules.tither.model.Tither;
import com.atendeai.modules.tither.repository.TitherRepository;
import com.atendeai.modules.tithepayment.dto.MarkPaidRequest;
import com.atendeai.modules.tithepayment.dto.MonthlyHistoryItem;
import com.atendeai.modules.tithepayment.dto.MonthlySummaryResponse;
import com.atendeai.modules.tithepayment.dto.MonthlyTitherRow;
import com.atendeai.modules.tithepayment.model.TithePayment;
import com.atendeai.modules.tithepayment.repository.TithePaymentRepository;
import com.atendeai.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TithePaymentService {

    private final TithePaymentRepository paymentRepository;
    private final TitherRepository titherRepository;
    private final UserRepository userRepository;

    public List<MonthlyTitherRow> listMonthly(String month, UUID churchId, String q, String status) {
        LocalDate monthDate = parseMonth(month);
        List<Tither> tithers = titherRepository.searchAll(StringUtils.hasText(q) ? q : "", churchId);
        List<UUID> titherIds = tithers.stream().map(Tither::getId).toList();

        Map<UUID, TithePayment> paymentByTither = paymentRepository
                .findByReferenceMonthAndTither_IdIn(monthDate, titherIds).stream()
                .collect(Collectors.toMap(p -> p.getTither().getId(), p -> p));

        List<MonthlyTitherRow> rows = new ArrayList<>();
        for (Tither t : tithers) {
            TithePayment payment = paymentByTither.get(t.getId());
            boolean paid = payment != null;
            if (status != null && !status.isBlank()) {
                boolean wantsPaid = status.equalsIgnoreCase("PAGO");
                if (wantsPaid != paid) continue;
            }
            rows.add(new MonthlyTitherRow(
                    t.getId(), t.getName(), t.getChurch().getId(), t.getChurch().getName(),
                    t.getReferenceAmount(),
                    paid ? "PAGO" : "PENDENTE",
                    paid ? payment.getId() : null,
                    paid ? payment.getAmount() : null,
                    paid ? payment.getPaidAt() : null
            ));
        }
        return rows;
    }

    public MonthlySummaryResponse summary(String month, UUID churchId) {
        LocalDate monthDate = parseMonth(month);
        long totalActive = churchId != null
                ? titherRepository.countByActiveTrueAndChurch_Id(churchId)
                : titherRepository.countByActiveTrue();
        long paidCount = paymentRepository.countByMonthAndChurch(monthDate, churchId);
        BigDecimal totalAmount = paymentRepository.sumAmountByMonth(monthDate, churchId);
        return new MonthlySummaryResponse(totalActive, paidCount, Math.max(0, totalActive - paidCount), totalAmount);
    }

    public List<MonthlyHistoryItem> history(int months, UUID churchId) {
        LocalDate currentMonth = YearMonth.now().atDay(1);
        LocalDate from = currentMonth.minusMonths(months - 1L);

        Map<LocalDate, BigDecimal> totals = paymentRepository.sumAmountGroupedByMonth(from, churchId).stream()
                .collect(Collectors.toMap(
                        TithePaymentRepository.MonthTotal::getMonth,
                        TithePaymentRepository.MonthTotal::getTotal));

        List<MonthlyHistoryItem> result = new ArrayList<>();
        for (int i = 0; i < months; i++) {
            LocalDate month = from.plusMonths(i);
            BigDecimal total = totals.getOrDefault(month, BigDecimal.ZERO);
            result.add(new MonthlyHistoryItem(month.toString().substring(0, 7), total));
        }
        return result;
    }

    @Transactional
    public MonthlyTitherRow markPaid(MarkPaidRequest req) {
        Tither tither = titherRepository.findById(req.titherId())
                .filter(Tither::isActive)
                .orElseThrow(() -> new BusinessException("Dizimista não encontrado", HttpStatus.NOT_FOUND));

        LocalDate monthDate = parseMonth(req.referenceMonth());
        BigDecimal amount = req.amount() != null ? req.amount()
                : (tither.getReferenceAmount() != null ? tither.getReferenceAmount() : BigDecimal.ZERO);
        Instant paidAt = req.paidAt() != null
                ? req.paidAt().atStartOfDay(ZoneOffset.UTC).toInstant()
                : Instant.now();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElse(null);

        TithePayment payment = paymentRepository.findByTither_IdAndReferenceMonth(tither.getId(), monthDate)
                .orElseGet(() -> TithePayment.builder().tither(tither).referenceMonth(monthDate).build());
        payment.setAmount(amount);
        payment.setPaidAt(paidAt);
        payment.setNotes(req.notes());
        payment.setRegisteredBy(user);
        payment = paymentRepository.save(payment);

        return new MonthlyTitherRow(
                tither.getId(), tither.getName(), tither.getChurch().getId(), tither.getChurch().getName(),
                tither.getReferenceAmount(), "PAGO", payment.getId(), payment.getAmount(), payment.getPaidAt());
    }

    @Transactional
    public void unmark(UUID titherId, String month) {
        LocalDate monthDate = parseMonth(month);
        TithePayment payment = paymentRepository.findByTither_IdAndReferenceMonth(titherId, monthDate)
                .orElseThrow(() -> new BusinessException("Pagamento não encontrado", HttpStatus.NOT_FOUND));
        paymentRepository.delete(payment);
    }

    private LocalDate parseMonth(String month) {
        try {
            return YearMonth.parse(month).atDay(1);
        } catch (Exception e) {
            throw new BusinessException("Mês inválido. Use o formato yyyy-MM");
        }
    }
}
