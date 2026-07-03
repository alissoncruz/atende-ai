package com.atendeai.modules.tithepayment.repository;

import com.atendeai.modules.tithepayment.model.TithePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TithePaymentRepository extends JpaRepository<TithePayment, UUID> {

    Optional<TithePayment> findByTither_IdAndReferenceMonth(UUID titherId, LocalDate referenceMonth);

    List<TithePayment> findByReferenceMonthAndTither_IdIn(LocalDate referenceMonth, List<UUID> titherIds);

    long countByReferenceMonth(LocalDate referenceMonth);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM TithePayment p WHERE p.referenceMonth = :month " +
           "AND (:churchId IS NULL OR p.tither.church.id = :churchId)")
    java.math.BigDecimal sumAmountByMonth(LocalDate month, UUID churchId);

    @Query("SELECT COUNT(p) FROM TithePayment p WHERE p.referenceMonth = :month " +
           "AND (:churchId IS NULL OR p.tither.church.id = :churchId)")
    long countByMonthAndChurch(LocalDate month, UUID churchId);

    @Query("SELECT p.referenceMonth as month, COALESCE(SUM(p.amount), 0) as total FROM TithePayment p " +
           "WHERE p.referenceMonth >= :from AND (:churchId IS NULL OR p.tither.church.id = :churchId) " +
           "GROUP BY p.referenceMonth ORDER BY p.referenceMonth ASC")
    List<MonthTotal> sumAmountGroupedByMonth(LocalDate from, UUID churchId);

    interface MonthTotal {
        LocalDate getMonth();
        java.math.BigDecimal getTotal();
    }
}
