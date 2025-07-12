package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public Appointment createAppointment(Appointment appointment) {
        log.info("Création d'un nouveau rendez-vous pour le patient ID: {} avec le médecin ID: {}", 
                appointment.getPatient().getId(), appointment.getDoctor().getId());
        
        // Vérifier la disponibilité du créneau
        if (!isTimeSlotAvailable(appointment.getDoctor(), appointment.getAppointmentDate(), 
                                appointment.getAppointmentDate().plusMinutes(appointment.getDurationMinutes()))) {
            throw new RuntimeException("Créneau horaire non disponible pour ce médecin");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setCreatedDate(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Rendez-vous créé avec succès avec l'ID: {}", savedAppointment.getId());
        return savedAppointment;
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + patientId));
        return appointmentRepository.findByPatientOrderByAppointmentDateDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return appointmentRepository.findByDoctorOrderByAppointmentDate(doctor);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getTodayAppointments() {
        return appointmentRepository.findTodayAppointments();
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return appointmentRepository.findAppointmentsByDoctorAndDate(doctor, date);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findUpcomingAppointments(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findPendingAppointments(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Appointment> getMissedAppointments() {
        return appointmentRepository.findMissedAppointments(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Appointment> getUrgentAppointments() {
        return appointmentRepository.findByIsUrgent(true);
    }

    @Transactional(readOnly = true)
    public Page<Appointment> searchAppointments(String searchTerm, Pageable pageable) {
        return appointmentRepository.searchAppointments(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findAppointmentsBetweenDates(startDate, endDate);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment appointment = getAppointmentById(id);
        
        // Vérifier la disponibilité du nouveau créneau si la date/heure change
        if (!appointment.getAppointmentDate().equals(appointmentDetails.getAppointmentDate()) ||
            !appointment.getDoctor().getId().equals(appointmentDetails.getDoctor().getId())) {
            
            if (!isTimeSlotAvailable(appointmentDetails.getDoctor(), appointmentDetails.getAppointmentDate(),
                                   appointmentDetails.getAppointmentDate().plusMinutes(appointmentDetails.getDurationMinutes()))) {
                throw new RuntimeException("Nouveau créneau horaire non disponible");
            }
        }
        
        appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
        appointment.setDoctor(appointmentDetails.getDoctor());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setDurationMinutes(appointmentDetails.getDurationMinutes());
        appointment.setNotes(appointmentDetails.getNotes());
        appointment.setIsUrgent(appointmentDetails.getIsUrgent());
        
        return appointmentRepository.save(appointment);
    }

    public Appointment confirmAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointment.setConfirmedDate(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Rendez-vous confirmé avec l'ID: {}", id);
        return savedAppointment;
    }

    public Appointment cancelAppointment(Long id, String reason) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledDate(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Rendez-vous annulé avec l'ID: {}", id);
        return savedAppointment;
    }

    public Appointment completeAppointment(Long id, String notes) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setNotes(notes);
        appointment.setCompletedDate(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Rendez-vous terminé avec l'ID: {}", id);
        return savedAppointment;
    }

    public Appointment markAsNoShow(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        return appointmentRepository.save(appointment);
    }

    public Appointment rescheduleAppointment(Long id, LocalDateTime newDateTime) {
        Appointment appointment = getAppointmentById(id);
        
        // Vérifier la disponibilité du nouveau créneau
        if (!isTimeSlotAvailable(appointment.getDoctor(), newDateTime, 
                                newDateTime.plusMinutes(appointment.getDurationMinutes()))) {
            throw new RuntimeException("Nouveau créneau horaire non disponible");
        }
        
        appointment.setAppointmentDate(newDateTime);
        appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
        
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
        log.info("Rendez-vous supprimé avec l'ID: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        Long conflictingAppointments = appointmentRepository.countConflictingAppointments(doctor, startTime, endTime);
        return conflictingAppointments == 0;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAppointmentStatsByDoctor() {
        return appointmentRepository.countAppointmentsByDoctor();
    }

    @Transactional(readOnly = true)
    public Long countTodayAppointments() {
        return (long) appointmentRepository.findTodayAppointments().size();
    }

    @Transactional(readOnly = true)
    public Long countAppointmentsByStatus(String status) {
        return (long) appointmentRepository.findByStatus(status).size();
    }

    public Appointment markAsUrgent(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setIsUrgent(true);
        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatusAndDateRange(String status, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByStatusAndAppointmentDateBetween(status, start, end);
    }
}