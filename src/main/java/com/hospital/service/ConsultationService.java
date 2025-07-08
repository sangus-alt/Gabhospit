package com.hospital.service;

import com.hospital.entity.Consultation;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public Consultation createConsultation(Consultation consultation) {
        log.info("Création d'une nouvelle consultation pour le patient ID: {}", consultation.getPatient().getId());
        
        consultation.setConsultationDate(LocalDateTime.now());
        consultation.setStatus("IN_PROGRESS");
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Consultation créée avec succès avec l'ID: {}", savedConsultation.getId());
        return savedConsultation;
    }

    @Transactional(readOnly = true)
    public Consultation getConsultationById(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation non trouvée avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Consultation> getAllConsultations(Pageable pageable) {
        return consultationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + patientId));
        return consultationRepository.findByPatientOrderByConsultationDateDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return consultationRepository.findByDoctorOrderByConsultationDateDesc(doctor);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getTodayConsultations() {
        return consultationRepository.findTodayConsultations();
    }

    @Transactional(readOnly = true)
    public List<Consultation> getTodayConsultationsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return consultationRepository.findTodayConsultationsByDoctor(doctor);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return consultationRepository.findConsultationsBetweenDates(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByType(String consultationType) {
        return consultationRepository.findByConsultationType(consultationType);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getConsultationsByStatus(String status) {
        return consultationRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getUrgentConsultations() {
        return consultationRepository.findByIsUrgent(true);
    }

    @Transactional(readOnly = true)
    public Page<Consultation> searchConsultations(String searchTerm, Pageable pageable) {
        return consultationRepository.searchConsultations(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getRecentConsultations(Pageable pageable) {
        return consultationRepository.findRecentConsultations(pageable);
    }

    @Transactional(readOnly = true)
    public List<Consultation> getUpcomingConsultationsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return consultationRepository.findUpcomingConsultationsByDoctor(doctor, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Consultation> getCompletedConsultations() {
        return consultationRepository.findCompletedConsultations();
    }

    public Consultation updateConsultation(Long id, Consultation consultationDetails) {
        Consultation consultation = getConsultationById(id);
        
        consultation.setChiefComplaint(consultationDetails.getChiefComplaint());
        consultation.setPresentIllness(consultationDetails.getPresentIllness());
        consultation.setPhysicalExamination(consultationDetails.getPhysicalExamination());
        consultation.setDiagnosis(consultationDetails.getDiagnosis());
        consultation.setTreatmentPlan(consultationDetails.getTreatmentPlan());
        consultation.setNotes(consultationDetails.getNotes());
        consultation.setFollowUpDate(consultationDetails.getFollowUpDate());
        consultation.setConsultationType(consultationDetails.getConsultationType());
        
        return consultationRepository.save(consultation);
    }

    public Consultation completeConsultation(Long id, String diagnosis, String treatmentPlan, String notes) {
        Consultation consultation = getConsultationById(id);
        
        consultation.setDiagnosis(diagnosis);
        consultation.setTreatmentPlan(treatmentPlan);
        consultation.setNotes(notes);
        consultation.setStatus("COMPLETED");
        consultation.setEndTime(LocalDateTime.now());
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Consultation terminée avec l'ID: {}", savedConsultation.getId());
        return savedConsultation;
    }

    public Consultation markAsUrgent(Long id) {
        Consultation consultation = getConsultationById(id);
        consultation.setIsUrgent(true);
        return consultationRepository.save(consultation);
    }

    public Consultation updateConsultationStatus(Long id, String status) {
        Consultation consultation = getConsultationById(id);
        consultation.setStatus(status);
        return consultationRepository.save(consultation);
    }

    public void deleteConsultation(Long id) {
        Consultation consultation = getConsultationById(id);
        consultationRepository.delete(consultation);
        log.info("Consultation supprimée avec l'ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getConsultationStatsByDoctor() {
        return consultationRepository.countConsultationsByDoctor();
    }

    public Consultation scheduleFollowUp(Long consultationId, LocalDateTime followUpDate) {
        Consultation consultation = getConsultationById(consultationId);
        consultation.setFollowUpDate(followUpDate);
        consultation.setRequiresFollowUp(true);
        return consultationRepository.save(consultation);
    }

    @Transactional(readOnly = true)
    public Long countConsultationsToday() {
        return (long) consultationRepository.findTodayConsultations().size();
    }

    @Transactional(readOnly = true)
    public Long countConsultationsByStatus(String status) {
        return (long) consultationRepository.findByStatus(status).size();
    }
}