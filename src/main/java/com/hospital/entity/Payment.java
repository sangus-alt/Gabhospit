package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Payment extends BaseEntity {

    @Column(name = "payment_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de paiement ne peut pas être vide")
    private String paymentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @NotNull(message = "La facture est obligatoire")
    private Bill bill;

    @Column(name = "payment_date", nullable = false)
    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDateTime paymentDate;

    @Column(name = "amount", nullable = false)
    @DecimalMin(value = "0.01", message = "Le montant doit être positif")
    @NotNull(message = "Le montant est obligatoire")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    @NotNull(message = "Le mode de paiement est obligatoire")
    private PaymentMethod paymentMethod;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "check_number", length = 50)
    private String checkNumber;

    @Column(name = "card_number", length = 20)
    private String cardNumber; // Masqué pour la sécurité

    @Column(name = "card_type", length = 50)
    private String cardType;

    @Column(name = "authorization_code", length = 100)
    private String authorizationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "received_by", length = 100)
    private String receivedBy;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(name = "is_refunded")
    private Boolean isRefunded = false;

    @Column(name = "refund_amount")
    @DecimalMin(value = "0.0", message = "Le montant du remboursement ne peut pas être négatif")
    private Double refundAmount = 0.0;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Column(name = "currency", length = 10)
    private String currency = "EUR";

    @Column(name = "exchange_rate")
    @DecimalMin(value = "0.0", message = "Le taux de change ne peut pas être négatif")
    private Double exchangeRate = 1.0;

    @PrePersist
    public void prePersist() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }

    public enum PaymentMethod {
        CASH("Espèces"),
        CREDIT_CARD("Carte de crédit"),
        DEBIT_CARD("Carte de débit"),
        BANK_TRANSFER("Virement bancaire"),
        CHECK("Chèque"),
        ONLINE_PAYMENT("Paiement en ligne"),
        MOBILE_PAYMENT("Paiement mobile"),
        INSURANCE("Assurance"),
        INSTALLMENT("Versement échelonné"),
        OTHER("Autre");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentStatus {
        PENDING("En attente"),
        PROCESSING("En traitement"),
        COMPLETED("Terminé"),
        FAILED("Échoué"),
        CANCELLED("Annulé"),
        REFUNDED("Remboursé");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}