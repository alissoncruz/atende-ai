package com.atendeai.modules.tither.model;

import com.atendeai.modules.church.model.Church;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tithers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tither {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    private String phone;
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "zip_code")
    private String zipCode;
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "reference_amount", precision = 10, scale = 2)
    private BigDecimal referenceAmount;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
