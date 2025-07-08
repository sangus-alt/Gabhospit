package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Rendez-vous par patient
    List<Appointment> findByPatientOrderByAppointmentDateDesc(Patient patient);

    // Rendez-vous par médecin
    List<Appointment> findByDoctorOrderByAppointmentDate(Doctor doctor);

    // Rendez-vous d'aujourd'hui
    @Query("SELECT a FROM Appointment a WHERE DATE(a.appointmentDate) = CURRENT_DATE")
    List<Appointment> findTodayAppointments();

    // Rendez-vous par période
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);

    // Rendez-vous par médecin et date
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND DATE(a.appointmentDate) = :date")
    List<Appointment> findAppointmentsByDoctorAndDate(@Param("doctor") Doctor doctor, @Param("date") java.time.LocalDate date);

    // Vérifier disponibilité créneau
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND " +
           "a.appointmentDate BETWEEN :startTime AND :endTime AND a.status != 'CANCELLED'")
    Long countConflictingAppointments(@Param("doctor") Doctor doctor, 
                                    @Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime);

    // Rendez-vous par statut
    List<Appointment> findByStatus(String status);

    // Prochains rendez-vous
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate > :now ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingAppointments(@Param("now") LocalDateTime now);

    // Rendez-vous en attente
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' AND a.appointmentDate > :now")
    List<Appointment> findPendingAppointments(@Param("now") LocalDateTime now);

    // Rendez-vous manqués
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' AND a.appointmentDate < :now")
    List<Appointment> findMissedAppointments(@Param("now") LocalDateTime now);

    // Recherche dans les rendez-vous
    @Query("SELECT a FROM Appointment a WHERE " +
           "LOWER(a.reason) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Appointment> searchAppointments(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Statistiques par médecin
    @Query("SELECT d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d GROUP BY d.lastName")
    List<Object[]> countAppointmentsByDoctor();

    // Rendez-vous annulés
    List<Appointment> findByStatusAndAppointmentDateBetween(String status, LocalDateTime start, LocalDateTime end);

    // Rendez-vous d'urgence
    List<Appointment> findByIsUrgent(Boolean isUrgent);
}