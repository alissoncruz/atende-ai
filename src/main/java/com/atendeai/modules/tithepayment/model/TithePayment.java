package com.atendeai.modules.tithepayment.model;

import com.atendeai.modules.auth.model.User;
import com.atendeai.modules.tither.model.Tither;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tithe_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TithePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tither_id", nullable = false)
    private Tither tither;

    @Column(name = "reference_month", nullable = false)
    private LocalDate referenceMonth;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_at", nullable = false)
    @Builder.Default
    private Instant paidAt = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by")
    private User registeredBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
