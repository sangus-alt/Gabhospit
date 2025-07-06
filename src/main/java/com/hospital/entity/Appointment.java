package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Appointment extends BaseEntity {

    @Column(name = "appointment_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de rendez-vous ne peut pas être vide")
    private String appointmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Le médecin est obligatoire")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "appointment_date", nullable = false)
    @NotNull(message = "La date du rendez-vous est obligatoire")
    @Future(message = "La date du rendez-vous doit être dans le futur")
    private LocalDateTime appointmentDate;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    @NotNull(message = "Le type de rendez-vous est obligatoire")
    private AppointmentType appointmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "patient_arrived")
    private Boolean patientArrived = false;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "consultation_fee")
    @DecimalMin(value = "0.0", message = "Les honoraires ne peuvent pas être négatifs")
    private Double consultationFee;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

    @Column(name = "reminder_sent_date")
    private LocalDateTime reminderSentDate;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Column(name = "rescheduled_from")
    private LocalDateTime rescheduledFrom;

    @Column(name = "rescheduled_reason", length = 500)
    private String rescheduledReason;

    @Column(name = "priority")
    @Min(value = 1, message = "La priorité doit être d'au moins 1")
    @Max(value = 5, message = "La priorité ne peut pas dépasser 5")
    private Integer priority = 3; // 1 = Très urgent, 5 = Pas urgent

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Consultation consultation;

    @PrePersist
    public void prePersist() {
        if (durationMinutes == null) {
            durationMinutes = 30; // Durée par défaut
        }
    }

    public enum AppointmentType {
        CONSULTATION("Consultation"),
        FOLLOW_UP("Suivi"),
        EMERGENCY("Urgence"),
        ROUTINE_CHECK("Contrôle routine"),
        SPECIALIST("Spécialiste"),
        SURGERY("Chirurgie"),
        DIAGNOSTIC("Diagnostic");

        private final String displayName;

        AppointmentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AppointmentStatus {
        SCHEDULED("Planifié"),
        CONFIRMED("Confirmé"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        CANCELLED("Annulé"),
        NO_SHOW("Absent"),
        RESCHEDULED("Reprogrammé");

        private final String displayName;

        AppointmentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}