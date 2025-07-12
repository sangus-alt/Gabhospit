package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "lab_orders")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabOrder extends BaseEntity {

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro de commande ne peut pas être vide")
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin prescripteur est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "order_date", nullable = false)
    @NotNull(message = "La date de commande est obligatoire")
    private LocalDateTime orderDate;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "collected_date")
    private LocalDateTime collectedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.NORMAL;

    @Column(name = "clinical_information", length = 1000)
    private String clinicalInformation;

    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @Column(name = "specimen_type", length = 100)
    private String specimenType;

    @Column(name = "specimen_site", length = 100)
    private String specimenSite;

    @Column(name = "fasting_required")
    private Boolean fastingRequired = false;

    @Column(name = "notes", length = 1000)
    private String notes;

    // Nouveaux champs pour corriger les erreurs de compilation
    @Column(name = "sample_id", length = 50)
    private String sampleId;

    @Column(name = "collection_date")
    private LocalDateTime collectionDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "collected_by", length = 100)
    private String collectedBy;

    @Column(name = "processing_start_date")
    private LocalDateTime processingStartDate;

    @Column(name = "results", length = 2000)
    private String results;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LabTest> labTests;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LabResult> labResults;

    @PrePersist
    public void prePersist() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (isUrgent == null) {
            isUrgent = false;
        }
    }

    public enum OrderStatus {
        PENDING("En attente"),
        SCHEDULED("Programmée"),
        COLLECTED("Échantillon collecté"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminée"),
        CANCELLED("Annulée"),
        REJECTED("Rejetée");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Priority {
        ROUTINE("Routine"),
        NORMAL("Normal"),
        URGENT("Urgent"),
        STAT("Immédiat"),
        CRITICAL("Critique");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}