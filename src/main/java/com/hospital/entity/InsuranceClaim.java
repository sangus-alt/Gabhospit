package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_claims")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InsuranceClaim extends BaseEntity {

    @Column(name = "claim_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro de réclamation ne peut pas être vide")
    private String claimNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    @NotNull(message = "L'assurance est obligatoire")
    private Insurance insurance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @Column(name = "claim_date", nullable = false)
    @NotNull(message = "La date de réclamation est obligatoire")
    private LocalDateTime claimDate;

    @Column(name = "amount_claimed", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le montant réclamé est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal amountClaimed;

    @Column(name = "amount_approved", precision = 10, scale = 2)
    private BigDecimal amountApproved;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @PrePersist
    public void prePersist() {
        if (claimDate == null) {
            claimDate = LocalDateTime.now();
        }
    }

    public enum ClaimStatus {
        SUBMITTED("Soumise"),
        UNDER_REVIEW("En révision"),
        APPROVED("Approuvée"),
        PARTIALLY_APPROVED("Partiellement approuvée"),
        REJECTED("Rejetée"),
        PAID("Payée"),
        CANCELLED("Annulée");

        private final String displayName;

        ClaimStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}