package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_management")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QueueManagement extends BaseEntity {

    @Column(name = "queue_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Le numéro de file d'attente ne peut pas être vide")
    private String queueNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Le patient est obligatoire")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull(message = "Le département est obligatoire")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "service_type", nullable = false, length = 100)
    @NotBlank(message = "Le type de service ne peut pas être vide")
    private String serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "queue_type", nullable = false)
    @NotNull(message = "Le type de file d'attente est obligatoire")
    private QueueType queueType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @NotNull(message = "La priorité est obligatoire")
    private Priority priority;

    @Column(name = "arrival_time", nullable = false)
    @NotNull(message = "L'heure d'arrivée est obligatoire")
    private LocalDateTime arrivalTime;

    @Column(name = "estimated_wait_time")
    private Integer estimatedWaitTime; // En minutes

    @Column(name = "called_time")
    private LocalDateTime calledTime;

    @Column(name = "service_start_time")
    private LocalDateTime serviceStartTime;

    @Column(name = "service_end_time")
    private LocalDateTime serviceEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QueueStatus status = QueueStatus.WAITING;

    @Column(name = "position_in_queue")
    private Integer positionInQueue;

    @Column(name = "counter_number", length = 20)
    private String counterNumber;

    @Column(name = "called_by", length = 100)
    private String calledBy;

    @Column(name = "no_show")
    private Boolean noShow = false;

    @Column(name = "rescheduled")
    private Boolean rescheduled = false;

    @Column(name = "reschedule_reason", length = 500)
    private String rescheduleReason;

    @Column(name = "sms_sent")
    private Boolean smsSent = false;

    @Column(name = "email_sent")
    private Boolean emailSent = false;

    @Column(name = "notification_sent_time")
    private LocalDateTime notificationSentTime;

    @Column(name = "feedback_rating")
    @Min(value = 1, message = "La note doit être d'au moins 1")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Integer feedbackRating;

    @Column(name = "feedback_comments", length = 1000)
    private String feedbackComments;

    @Column(name = "special_requirements", length = 500)
    private String specialRequirements;

    @Column(name = "accompanied_by", length = 200)
    private String accompaniedBy;

    @Column(name = "mobility_assistance_required")
    private Boolean mobilityAssistanceRequired = false;

    @Column(name = "language_preference", length = 50)
    private String languagePreference;

    @Column(name = "notes", length = 1000)
    private String notes;

    @PrePersist
    public void prePersist() {
        if (arrivalTime == null) {
            arrivalTime = LocalDateTime.now();
        }
    }

    public enum QueueType {
        WALK_IN("Sans rendez-vous"),
        APPOINTMENT("Avec rendez-vous"),
        EMERGENCY("Urgence"),
        FOLLOW_UP("Suivi"),
        PROCEDURE("Procédure"),
        CONSULTATION("Consultation"),
        LABORATORY("Laboratoire"),
        PHARMACY("Pharmacie");

        private final String displayName;

        QueueType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Priority {
        CRITICAL("Critique"),
        HIGH("Élevée"),
        MEDIUM("Moyenne"),
        LOW("Basse"),
        ROUTINE("Routine");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum QueueStatus {
        WAITING("En attente"),
        CALLED("Appelé"),
        IN_SERVICE("En service"),
        COMPLETED("Terminé"),
        NO_SHOW("Absent"),
        CANCELLED("Annulé"),
        TRANSFERRED("Transféré");

        private final String displayName;

        QueueStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}