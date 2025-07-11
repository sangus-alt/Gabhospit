package com.hospital.service;

import com.hospital.entity.MedicalCertificate;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.repository.MedicalCertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedicalCertificateService {

    private final MedicalCertificateRepository medicalCertificateRepository;

    /**
     * Créer un nouveau certificat médical
     */
    public MedicalCertificate createMedicalCertificate(MedicalCertificate certificate) {
        log.info("Création d'un nouveau certificat médical pour le patient ID: {}", 
                certificate.getPatient().getId());
        
        // Générer un numéro de certificat unique
        certificate.setCertificateNumber(generateCertificateNumber());
        certificate.setIssueDate(LocalDateTime.now());
        certificate.setStatus(MedicalCertificate.CertificateStatus.ACTIVE);
        
        MedicalCertificate savedCertificate = medicalCertificateRepository.save(certificate);
        log.info("Certificat médical créé avec succès avec l'ID: {} et numéro: {}", 
                savedCertificate.getId(), savedCertificate.getCertificateNumber());
        
        return savedCertificate;
    }

    /**
     * Mettre à jour un certificat médical existant
     */
    public MedicalCertificate updateMedicalCertificate(Long id, MedicalCertificate certificate) {
        log.info("Mise à jour du certificat médical avec l'ID: {}", id);
        
        MedicalCertificate existingCertificate = getMedicalCertificateById(id);
        
        existingCertificate.setCertificateType(certificate.getCertificateType());
        existingCertificate.setDiagnosis(certificate.getDiagnosis());
        existingCertificate.setTreatment(certificate.getTreatment());
        existingCertificate.setRecommendations(certificate.getRecommendations());
        existingCertificate.setRestPeriod(certificate.getRestPeriod());
        existingCertificate.setRestStartDate(certificate.getRestStartDate());
        existingCertificate.setRestEndDate(certificate.getRestEndDate());
        existingCertificate.setFollowUpDate(certificate.getFollowUpDate());
        existingCertificate.setLimitations(certificate.getLimitations());
        existingCertificate.setSpecialInstructions(certificate.getSpecialInstructions());
        existingCertificate.setIsForEmployer(certificate.getIsForEmployer());
        existingCertificate.setIsForInsurance(certificate.getIsForInsurance());
        existingCertificate.setIsForSports(certificate.getIsForSports());
        existingCertificate.setValidityPeriod(certificate.getValidityPeriod());
        existingCertificate.setNotes(certificate.getNotes());
        
        return medicalCertificateRepository.save(existingCertificate);
    }

    /**
     * Obtenir un certificat médical par ID
     */
    @Transactional(readOnly = true)
    public MedicalCertificate getMedicalCertificateById(Long id) {
        return medicalCertificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificat médical non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir un certificat médical par numéro
     */
    @Transactional(readOnly = true)
    public MedicalCertificate getMedicalCertificateByNumber(String certificateNumber) {
        return medicalCertificateRepository.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new RuntimeException("Certificat médical non trouvé avec le numéro: " + certificateNumber));
    }

    /**
     * Obtenir tous les certificats médicaux avec pagination
     */
    @Transactional(readOnly = true)
    public Page<MedicalCertificate> getAllMedicalCertificates(Pageable pageable) {
        return medicalCertificateRepository.findAll(pageable);
    }

    /**
     * Obtenir les certificats d'un patient
     */
    @Transactional(readOnly = true)
    public List<MedicalCertificate> getCertificatesByPatient(Patient patient) {
        return medicalCertificateRepository.findByPatient(patient);
    }

    /**
     * Obtenir les certificats émis par un médecin
     */
    @Transactional(readOnly = true)
    public List<MedicalCertificate> getCertificatesByDoctor(Doctor doctor) {
        return medicalCertificateRepository.findByDoctor(doctor);
    }

    /**
     * Obtenir les certificats par type
     */
    @Transactional(readOnly = true)
    public List<MedicalCertificate> getCertificatesByType(MedicalCertificate.CertificateType type) {
        return medicalCertificateRepository.findByCertificateType(type);
    }

    /**
     * Obtenir les certificats actifs
     */
    @Transactional(readOnly = true)
    public List<MedicalCertificate> getActiveCertificates() {
        return medicalCertificateRepository.findActiveWithinValidityPeriod();
    }

    /**
     * Obtenir les certificats expirés
     */
    @Transactional(readOnly = true)
    public List<MedicalCertificate> getExpiredCertificates() {
        return medicalCertificateRepository.findExpiredCertificates();
    }

    /**
     * Rechercher des certificats médicaux
     */
    @Transactional(readOnly = true)
    public Page<MedicalCertificate> searchMedicalCertificates(String searchTerm, Pageable pageable) {
        return medicalCertificateRepository.searchCertificates(searchTerm, pageable);
    }

    /**
     * Obtenir les certificats émis dans une période
     */
    @Transactional(readOnly = true)
    public List<MedicalCertificate> getCertificatesIssuedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalCertificateRepository.findByIssueDateBetween(startDate, endDate);
    }

    /**
     * Valider un certificat médical
     */
    public void validateCertificate(Long id) {
        log.info("Validation du certificat médical avec l'ID: {}", id);
        MedicalCertificate certificate = getMedicalCertificateById(id);
        certificate.setStatus(MedicalCertificate.CertificateStatus.VALIDATED);
        certificate.setValidationDate(LocalDateTime.now());
        medicalCertificateRepository.save(certificate);
    }

    /**
     * Annuler un certificat médical
     */
    public void cancelCertificate(Long id, String reason) {
        log.info("Annulation du certificat médical avec l'ID: {} pour raison: {}", id, reason);
        MedicalCertificate certificate = getMedicalCertificateById(id);
        certificate.setStatus(MedicalCertificate.CertificateStatus.CANCELLED);
        certificate.setCancellationReason(reason);
        certificate.setCancellationDate(LocalDateTime.now());
        medicalCertificateRepository.save(certificate);
    }

    /**
     * Supprimer un certificat médical
     */
    public void deleteMedicalCertificate(Long id) {
        log.info("Suppression du certificat médical avec l'ID: {}", id);
        MedicalCertificate certificate = getMedicalCertificateById(id);
        medicalCertificateRepository.delete(certificate);
    }

    /**
     * Compter les certificats émis par période
     */
    @Transactional(readOnly = true)
    public long countCertificatesIssuedSince(LocalDateTime date) {
        return medicalCertificateRepository.countByIssueDateAfter(date);
    }

    /**
     * Obtenir les statistiques des certificats par type
     */
    @Transactional(readOnly = true)
    public List<Object[]> getCertificateStatsByType() {
        return medicalCertificateRepository.getCertificateCountByType();
    }

    /**
     * Vérifier si un certificat est encore valide
     */
    @Transactional(readOnly = true)
    public boolean isCertificateValid(Long id) {
        MedicalCertificate certificate = getMedicalCertificateById(id);
        
        if (certificate.getStatus() != MedicalCertificate.CertificateStatus.ACTIVE && 
            certificate.getStatus() != MedicalCertificate.CertificateStatus.VALIDATED) {
            return false;
        }
        
        if (certificate.getValidityPeriod() != null) {
            LocalDate validUntil = certificate.getIssueDate().toLocalDate()
                    .plusDays(certificate.getValidityPeriod());
            return !LocalDate.now().isAfter(validUntil);
        }
        
        return true;
    }

    /**
     * Générer un numéro de certificat unique
     */
    private String generateCertificateNumber() {
        String prefix = "CERT";
        String year = String.valueOf(LocalDate.now().getYear());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + year + timestamp.substring(timestamp.length() - 6) + suffix;
    }
}