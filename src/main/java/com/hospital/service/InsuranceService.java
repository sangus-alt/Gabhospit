package com.hospital.service;

import com.hospital.entity.Insurance;
import com.hospital.entity.InsuranceClaim;
import com.hospital.entity.Patient;
import com.hospital.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    /**
     * Créer une nouvelle compagnie d'assurance
     */
    public Insurance createInsurance(Insurance insurance) {
        log.info("Création d'une nouvelle compagnie d'assurance: {}", insurance.getCompanyName());
        
        // Valider l'unicité du nom de la compagnie
        validateCompanyName(insurance.getCompanyName());
        
        insurance.setRegistrationDate(LocalDateTime.now());
        insurance.setIsActive(true);
        
        Insurance savedInsurance = insuranceRepository.save(insurance);
        log.info("Compagnie d'assurance créée avec succès avec l'ID: {}", savedInsurance.getId());
        
        return savedInsurance;
    }

    /**
     * Mettre à jour une compagnie d'assurance existante
     */
    public Insurance updateInsurance(Long id, Insurance insurance) {
        log.info("Mise à jour de la compagnie d'assurance avec l'ID: {}", id);
        
        Insurance existingInsurance = getInsuranceById(id);
        
        existingInsurance.setCompanyName(insurance.getCompanyName());
        existingInsurance.setInsuranceType(insurance.getInsuranceType());
        existingInsurance.setContactPerson(insurance.getContactPerson());
        existingInsurance.setPhoneNumber(insurance.getPhoneNumber());
        existingInsurance.setEmail(insurance.getEmail());
        existingInsurance.setAddress(insurance.getAddress());
        existingInsurance.setCity(insurance.getCity());
        existingInsurance.setPostalCode(insurance.getPostalCode());
        existingInsurance.setCountry(insurance.getCountry());
        existingInsurance.setWebsite(insurance.getWebsite());
        existingInsurance.setContractStartDate(insurance.getContractStartDate());
        existingInsurance.setContractEndDate(insurance.getContractEndDate());
        existingInsurance.setCoveragePercentage(insurance.getCoveragePercentage());
        existingInsurance.setMaxCoverageAmount(insurance.getMaxCoverageAmount());
        existingInsurance.setDeductible(insurance.getDeductible());
        existingInsurance.setNetworkType(insurance.getNetworkType());
        existingInsurance.setPriorAuthRequired(insurance.getPriorAuthRequired());
        existingInsurance.setEmergencyCoverage(insurance.getEmergencyCoverage());
        existingInsurance.setOutpatientCoverage(insurance.getOutpatientCoverage());
        existingInsurance.setInpatientCoverage(insurance.getInpatientCoverage());
        existingInsurance.setPharmacyCoverage(insurance.getPharmacyCoverage());
        existingInsurance.setDentalCoverage(insurance.getDentalCoverage());
        existingInsurance.setVisionCoverage(insurance.getVisionCoverage());
        existingInsurance.setMentalHealthCoverage(insurance.getMentalHealthCoverage());
        existingInsurance.setPreventiveCoverage(insurance.getPreventiveCoverage());
        existingInsurance.setCoveredServices(insurance.getCoveredServices());
        existingInsurance.setExcludedServices(insurance.getExcludedServices());
        existingInsurance.setClaimsProcessingTime(insurance.getClaimsProcessingTime());
        existingInsurance.setPaymentTerms(insurance.getPaymentTerms());
        existingInsurance.setNotes(insurance.getNotes());
        existingInsurance.setIsActive(insurance.getIsActive());
        
        return insuranceRepository.save(existingInsurance);
    }

    /**
     * Obtenir une compagnie d'assurance par ID
     */
    @Transactional(readOnly = true)
    public Insurance getInsuranceById(Long id) {
        return insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compagnie d'assurance non trouvée avec l'ID: " + id));
    }

    /**
     * Obtenir toutes les compagnies d'assurance avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Insurance> getAllInsurances(Pageable pageable) {
        return insuranceRepository.findAll(pageable);
    }

    /**
     * Rechercher des compagnies d'assurance
     */
    @Transactional(readOnly = true)
    public Page<Insurance> searchInsurances(String searchTerm, Pageable pageable) {
        return insuranceRepository.searchInsurances(searchTerm, pageable);
    }

    /**
     * Obtenir les compagnies d'assurance actives
     */
    @Transactional(readOnly = true)
    public List<Insurance> getActiveInsurances() {
        return insuranceRepository.findActiveInsurances();
    }

    /**
     * Obtenir les compagnies par type d'assurance
     */
    @Transactional(readOnly = true)
    public List<Insurance> getInsurancesByType(Insurance.InsuranceType type) {
        return insuranceRepository.findByInsuranceType(type);
    }

    /**
     * Obtenir les compagnies par type de réseau
     */
    @Transactional(readOnly = true)
    public List<Insurance> getInsurancesByNetworkType(String networkType) {
        return insuranceRepository.findByNetworkName(networkType);
    }

    /**
     * Obtenir les compagnies avec couverture d'urgence
     */
    @Transactional(readOnly = true)
    public List<Insurance> getInsurancesWithEmergencyCoverage() {
        return insuranceRepository.findByEmergencyCoverageTrue();
    }

    /**
     * Obtenir les contrats expirant bientôt
     */
    @Transactional(readOnly = true)
    public List<Insurance> getContractsExpiringBefore(LocalDate date) {
        return insuranceRepository.findContractsExpiringBefore(date);
    }

    /**
     * Vérifier l'éligibilité d'un patient pour une assurance
     */
    @Transactional(readOnly = true)
    public boolean checkEligibility(Patient patient, Long insuranceId) {
        Insurance insurance = getInsuranceById(insuranceId);
        
        // Vérifier si l'assurance est active
        if (!insurance.getIsActive()) {
            log.warn("Assurance inactive pour l'ID: {}", insuranceId);
            return false;
        }
        
        // Vérifier si le contrat est valide
        LocalDate today = LocalDate.now();
        if (insurance.getContractStartDate() != null && today.isBefore(insurance.getContractStartDate())) {
            log.warn("Contrat d'assurance pas encore démarré pour l'ID: {}", insuranceId);
            return false;
        }
        
        if (insurance.getContractEndDate() != null && today.isAfter(insurance.getContractEndDate())) {
            log.warn("Contrat d'assurance expiré pour l'ID: {}", insuranceId);
            return false;
        }
        
        // Vérifications additionnelles spécifiques au patient peuvent être ajoutées ici
        
        return true;
    }

    /**
     * Calculer la couverture pour un montant donné
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateCoverage(Long insuranceId, BigDecimal totalAmount) {
        Insurance insurance = getInsuranceById(insuranceId);
        
        BigDecimal deductible = insurance.getDeductible() != null ? insurance.getDeductible() : BigDecimal.ZERO;
        BigDecimal amountAfterDeductible = totalAmount.subtract(deductible);
        
        if (amountAfterDeductible.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal coveragePercentage = insurance.getCoveragePercentage() != null ? 
                insurance.getCoveragePercentage() : BigDecimal.ZERO;
        
        BigDecimal coveredAmount = amountAfterDeductible
                .multiply(coveragePercentage)
                .divide(BigDecimal.valueOf(100));
        
        // Vérifier le montant maximum de couverture
        if (insurance.getMaxCoverageAmount() != null && 
            coveredAmount.compareTo(insurance.getMaxCoverageAmount()) > 0) {
            coveredAmount = insurance.getMaxCoverageAmount();
        }
        
        return coveredAmount;
    }

    /**
     * Désactiver une compagnie d'assurance
     */
    public void deactivateInsurance(Long id) {
        log.info("Désactivation de la compagnie d'assurance avec l'ID: {}", id);
        Insurance insurance = getInsuranceById(id);
        insurance.setIsActive(false);
        insuranceRepository.save(insurance);
    }

    /**
     * Réactiver une compagnie d'assurance
     */
    public void reactivateInsurance(Long id) {
        log.info("Réactivation de la compagnie d'assurance avec l'ID: {}", id);
        Insurance insurance = getInsuranceById(id);
        insurance.setIsActive(true);
        insuranceRepository.save(insurance);
    }

    /**
     * Supprimer une compagnie d'assurance
     */
    public void deleteInsurance(Long id) {
        log.info("Suppression de la compagnie d'assurance avec l'ID: {}", id);
        Insurance insurance = getInsuranceById(id);
        insuranceRepository.delete(insurance);
    }

    /**
     * Compter les compagnies d'assurance actives
     */
    @Transactional(readOnly = true)
    public long countActiveInsurances() {
        return insuranceRepository.countActiveInsurances();
    }

    /**
     * Obtenir les statistiques des assurances par type
     */
    @Transactional(readOnly = true)
    public List<Object[]> getInsuranceStatsByType() {
        return insuranceRepository.getInsuranceCountByType();
    }

    /**
     * Obtenir les statistiques des assurances par type de réseau
     */
    @Transactional(readOnly = true)
    public List<Object[]> getInsuranceStatsByNetworkType() {
        return insuranceRepository.getInsuranceCountByNetworkName();
    }

    /**
     * Valider l'unicité du nom de la compagnie
     */
    private void validateCompanyName(String companyName) {
        if (insuranceRepository.findByCompanyName(companyName).isPresent()) {
            throw new RuntimeException("Une compagnie d'assurance avec ce nom existe déjà: " + companyName);
        }
    }
}