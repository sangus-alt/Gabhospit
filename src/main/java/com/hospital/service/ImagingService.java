package com.hospital.service;

import com.hospital.entity.ImagingResult;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.repository.ImagingRepository;
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
public class ImagingService {

    private final ImagingRepository imagingRepository;

    /**
     * Créer un nouvel ordre d'imagerie
     */
    public ImagingResult createImagingOrder(ImagingResult imagingResult) {
        log.info("Création d'un nouvel ordre d'imagerie pour le patient ID: {}", 
                imagingResult.getPatient().getId());
        
        // Générer un numéro d'ordre unique
        imagingResult.setOrderNumber(generateOrderNumber());
        imagingResult.setOrderDate(LocalDateTime.now());
        imagingResult.setResultStatus(ImagingResult.ResultStatus.PENDING);
        
        ImagingResult savedResult = imagingRepository.save(imagingResult);
        log.info("Ordre d'imagerie créé avec succès avec l'ID: {} et numéro: {}", 
                savedResult.getId(), savedResult.getOrderNumber());
        
        return savedResult;
    }

    /**
     * Mettre à jour un résultat d'imagerie existant
     */
    public ImagingResult updateImagingResult(Long id, ImagingResult imagingResult) {
        log.info("Mise à jour du résultat d'imagerie avec l'ID: {}", id);
        
        ImagingResult existingResult = getImagingResultById(id);
        
        existingResult.setImagingType(imagingResult.getImagingType());
        existingResult.setBodyPart(imagingResult.getBodyPart());
        existingResult.setUrgencyLevel(imagingResult.getUrgencyLevel());
        existingResult.setClinicalIndication(imagingResult.getClinicalIndication());
        existingResult.setContrast(imagingResult.getContrast());
        existingResult.setContrastType(imagingResult.getContrastType());
        existingResult.setPreparationInstructions(imagingResult.getPreparationInstructions());
        existingResult.setScheduledDate(imagingResult.getScheduledDate());
        existingResult.setCompletedDate(imagingResult.getCompletedDate());
        existingResult.setTechnician(imagingResult.getTechnician());
        existingResult.setRadiologist(imagingResult.getRadiologist());
        existingResult.setEquipmentUsed(imagingResult.getEquipmentUsed());
        existingResult.setProtocol(imagingResult.getProtocol());
        existingResult.setFindings(imagingResult.getFindings());
        existingResult.setImpression(imagingResult.getImpression());
        existingResult.setRecommendations(imagingResult.getRecommendations());
        existingResult.setImagePath(imagingResult.getImagePath());
        existingResult.setReportPath(imagingResult.getReportPath());
        existingResult.setDicomStudyId(imagingResult.getDicomStudyId());
        existingResult.setResultStatus(imagingResult.getResultStatus());
        existingResult.setNotes(imagingResult.getNotes());
        
        return imagingRepository.save(existingResult);
    }

    /**
     * Obtenir un résultat d'imagerie par ID
     */
    @Transactional(readOnly = true)
    public ImagingResult getImagingResultById(Long id) {
        return imagingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Résultat d'imagerie non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir un résultat d'imagerie par numéro d'ordre
     */
    @Transactional(readOnly = true)
    public ImagingResult getImagingResultByOrderNumber(String orderNumber) {
        return imagingRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Résultat d'imagerie non trouvé avec le numéro: " + orderNumber));
    }

    /**
     * Obtenir tous les résultats d'imagerie avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ImagingResult> getAllImagingResults(Pageable pageable) {
        return imagingRepository.findAll(pageable);
    }

    /**
     * Obtenir les résultats d'imagerie d'un patient
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getImagingResultsByPatient(Patient patient) {
        return imagingRepository.findByPatient(patient);
    }

    /**
     * Obtenir les résultats d'imagerie d'un médecin
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getImagingResultsByDoctor(Doctor doctor) {
        return imagingRepository.findByOrderingPhysician(doctor);
    }

    /**
     * Obtenir les résultats par type d'imagerie
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getImagingResultsByType(ImagingResult.ImagingType type) {
        return imagingRepository.findByImagingType(type);
    }

    /**
     * Obtenir les résultats par partie du corps
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getImagingResultsByBodyPart(String bodyPart) {
        return imagingRepository.findByBodyPart(bodyPart);
    }

    /**
     * Obtenir les résultats par statut
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getImagingResultsByStatus(ImagingResult.ResultStatus status) {
        return imagingRepository.findByResultStatus(status);
    }

    /**
     * Obtenir les examens programmés pour une date
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getScheduledExamsForDate(LocalDate date) {
        return imagingRepository.findScheduledForDate(date);
    }

    /**
     * Obtenir les examens urgents en attente
     */
    @Transactional(readOnly = true)
    public List<ImagingResult> getUrgentPendingExams() {
        return imagingRepository.findUrgentPendingExams();
    }

    /**
     * Rechercher des résultats d'imagerie
     */
    @Transactional(readOnly = true)
    public Page<ImagingResult> searchImagingResults(String searchTerm, Pageable pageable) {
        return imagingRepository.searchImagingResults(searchTerm, pageable);
    }

    /**
     * Programmer un examen d'imagerie
     */
    public void scheduleExam(Long id, LocalDateTime scheduledDate) {
        log.info("Programmation de l'examen d'imagerie ID: {} pour le {}", id, scheduledDate);
        
        ImagingResult result = getImagingResultById(id);
        result.setScheduledDate(scheduledDate);
        result.setResultStatus(ImagingResult.ResultStatus.IN_PROGRESS);
        
        imagingRepository.save(result);
    }

    /**
     * Marquer un examen comme en cours
     */
    public void startExam(Long id, String technician) {
        log.info("Démarrage de l'examen d'imagerie ID: {} par {}", id, technician);
        
        ImagingResult result = getImagingResultById(id);
        result.setResultStatus(ImagingResult.ResultStatus.IN_PROGRESS);
        result.setTechnician(technician);
        
        imagingRepository.save(result);
    }

    /**
     * Compléter un examen d'imagerie
     */
    public void completeExam(Long id, String imagePath, String dicomStudyId) {
        log.info("Finalisation de l'examen d'imagerie ID: {}", id);
        
        ImagingResult result = getImagingResultById(id);
        result.setResultStatus(ImagingResult.ResultStatus.COMPLETED);
        result.setCompletedDate(LocalDateTime.now());
        result.setImagePath(imagePath);
        result.setDicomStudyId(dicomStudyId);
        
        imagingRepository.save(result);
    }

    /**
     * Valider les résultats par un radiologue
     */
    public void validateResults(Long id, String radiologist, String findings, 
                              String impression, String recommendations) {
        log.info("Validation des résultats d'imagerie ID: {} par {}", id, radiologist);
        
        ImagingResult result = getImagingResultById(id);
        result.setResultStatus(ImagingResult.ResultStatus.COMPLETED);
        result.setRadiologist(radiologist);
        result.setFindings(findings);
        result.setImpression(impression);
        result.setRecommendations(recommendations);
        result.setInterpretationDate(LocalDateTime.now());
        
        imagingRepository.save(result);
    }

    /**
     * Finaliser le rapport
     */
    public void finalizeReport(Long id, String reportPath) {
        log.info("Finalisation du rapport d'imagerie ID: {}", id);
        
        ImagingResult result = getImagingResultById(id);
        result.setResultStatus(ImagingResult.ResultStatus.COMPLETED);
        result.setReportPath(reportPath);
        result.setReportDate(LocalDateTime.now());
        
        imagingRepository.save(result);
    }

    /**
     * Annuler un examen d'imagerie
     */
    public void cancelExam(Long id, String reason) {
        log.info("Annulation de l'examen d'imagerie ID: {} pour raison: {}", id, reason);
        
        ImagingResult result = getImagingResultById(id);
        result.setStatus(ImagingResult.ImagingStatus.CANCELLED);
        result.setNotes(result.getNotes() != null ? 
                result.getNotes() + "\nAnnulé: " + reason : "Annulé: " + reason);
        
        imagingRepository.save(result);
    }

    /**
     * Supprimer un résultat d'imagerie
     */
    public void deleteImagingResult(Long id) {
        log.info("Suppression du résultat d'imagerie avec l'ID: {}", id);
        ImagingResult result = getImagingResultById(id);
        imagingRepository.delete(result);
    }

    /**
     * Compter les examens par statut
     */
    @Transactional(readOnly = true)
    public long countExamsByStatus(ImagingResult.ImagingStatus status) {
        return imagingRepository.countByStatus(status);
    }

    /**
     * Obtenir les statistiques par type d'imagerie
     */
    @Transactional(readOnly = true)
    public List<Object[]> getImagingStatsByType() {
        return imagingRepository.getImagingCountByType();
    }

    /**
     * Obtenir les statistiques par statut
     */
    @Transactional(readOnly = true)
    public List<Object[]> getImagingStatsByStatus() {
        return imagingRepository.getImagingCountByStatus();
    }

    /**
     * Générer un numéro d'ordre unique
     */
    private String generateOrderNumber() {
        String prefix = "IMG";
        String year = String.valueOf(LocalDate.now().getYear());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + year + timestamp.substring(timestamp.length() - 6) + suffix;
    }
}