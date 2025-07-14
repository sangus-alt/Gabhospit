package com.hospital.service;

import com.hospital.entity.Vaccine;
import com.hospital.entity.VaccinationRecord;
import com.hospital.repository.VaccineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    /**
     * Créer un nouveau vaccin
     */
    public Vaccine createVaccine(Vaccine vaccine) {
        log.info("Création d'un nouveau vaccin: {}", vaccine.getName());
        
        // Valider l'unicité du nom
        validateVaccineName(vaccine.getName());
        
        vaccine.setCreationDate(LocalDateTime.now());
        vaccine.setIsActive(true);
        
        Vaccine savedVaccine = vaccineRepository.save(vaccine);
        log.info("Vaccin créé avec succès avec l'ID: {}", savedVaccine.getId());
        
        return savedVaccine;
    }

    /**
     * Mettre à jour un vaccin existant
     */
    public Vaccine updateVaccine(Long id, Vaccine vaccine) {
        log.info("Mise à jour du vaccin avec l'ID: {}", id);
        
        Vaccine existingVaccine = getVaccineById(id);
        
        existingVaccine.setName(vaccine.getName());
        existingVaccine.setDescription(vaccine.getDescription());
        existingVaccine.setManufacturer(vaccine.getManufacturer());
        existingVaccine.setDiseasesPrevented(vaccine.getDiseasesPrevented());
        existingVaccine.setAgeGroup(vaccine.getAgeGroup());
        existingVaccine.setDosage(vaccine.getDosage());
        existingVaccine.setAdministrationRoute(vaccine.getAdministrationRoute());
        existingVaccine.setStorageTemperature(vaccine.getStorageTemperature());
        existingVaccine.setShelfLife(vaccine.getShelfLife());
        existingVaccine.setContraindications(vaccine.getContraindications());
        existingVaccine.setSideEffects(vaccine.getSideEffects());
        existingVaccine.setPrice(vaccine.getPrice());
        existingVaccine.setStockQuantity(vaccine.getStockQuantity());
        existingVaccine.setMinimumStock(vaccine.getMinimumStock());
        existingVaccine.setIsActive(vaccine.getIsActive());
        
        return vaccineRepository.save(existingVaccine);
    }

    /**
     * Obtenir un vaccin par ID
     */
    @Transactional(readOnly = true)
    public Vaccine getVaccineById(Long id) {
        return vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccin non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir tous les vaccins avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Vaccine> getAllVaccines(Pageable pageable) {
        return vaccineRepository.findAll(pageable);
    }

    /**
     * Rechercher des vaccins
     */
    @Transactional(readOnly = true)
    public Page<Vaccine> searchVaccines(String searchTerm, Pageable pageable) {
        return vaccineRepository.searchVaccines(searchTerm, pageable);
    }

    /**
     * Obtenir les vaccins actifs
     */
    @Transactional(readOnly = true)
    public List<Vaccine> getActiveVaccines() {
        return vaccineRepository.findActiveVaccines();
    }

    /**
     * Obtenir les vaccins par fabricant
     */
    @Transactional(readOnly = true)
    public List<Vaccine> getVaccinesByManufacturer(String manufacturer) {
        return vaccineRepository.findByManufacturer(manufacturer);
    }

    /**
     * Obtenir les vaccins par groupe d'âge
     */
    @Transactional(readOnly = true)
    public List<Vaccine> getVaccinesByAgeGroup(String ageGroup) {
        return vaccineRepository.findByAgeGroup(ageGroup);
    }

    /**
     * Obtenir les vaccins avec stock bas
     */
    @Transactional(readOnly = true)
    public List<Vaccine> getLowStockVaccines() {
        return vaccineRepository.findLowStockVaccines();
    }

    /**
     * Obtenir les vaccins expirant bientôt
     */
    @Transactional(readOnly = true)
    public List<Vaccine> getExpiringSoonVaccines(LocalDate date) {
        return vaccineRepository.findExpiringSoonVaccines(date);
    }

    /**
     * Mettre à jour le stock d'un vaccin
     */
    public void updateVaccineStock(Long vaccineId, Integer newQuantity) {
        log.info("Mise à jour du stock du vaccin ID: {} vers {}", vaccineId, newQuantity);
        
        Vaccine vaccine = getVaccineById(vaccineId);
        vaccine.setStockQuantity(newQuantity);
        vaccine.setLastStockUpdate(LocalDateTime.now());
        
        vaccineRepository.save(vaccine);
        
        // Vérifier si le stock est faible
        if (newQuantity <= vaccine.getMinimumStock()) {
            log.warn("Stock faible pour le vaccin {}: {} unités restantes", 
                    vaccine.getName(), newQuantity);
        }
    }

    /**
     * Désactiver un vaccin
     */
    public void deactivateVaccine(Long id) {
        log.info("Désactivation du vaccin avec l'ID: {}", id);
        Vaccine vaccine = getVaccineById(id);
        vaccine.setIsActive(false);
        vaccineRepository.save(vaccine);
    }

    /**
     * Réactiver un vaccin
     */
    public void reactivateVaccine(Long id) {
        log.info("Réactivation du vaccin avec l'ID: {}", id);
        Vaccine vaccine = getVaccineById(id);
        vaccine.setIsActive(true);
        vaccineRepository.save(vaccine);
    }

    /**
     * Supprimer un vaccin
     */
    public void deleteVaccine(Long id) {
        log.info("Suppression du vaccin avec l'ID: {}", id);
        Vaccine vaccine = getVaccineById(id);
        vaccineRepository.delete(vaccine);
    }

    /**
     * Compter les vaccins actifs
     */
    @Transactional(readOnly = true)
    public long countActiveVaccines() {
        return vaccineRepository.countActiveVaccines();
    }

    /**
     * Obtenir les statistiques des vaccins par fabricant
     */
    @Transactional(readOnly = true)
    public List<Object[]> getVaccineStatsByManufacturer() {
        return vaccineRepository.getVaccineCountByManufacturer();
    }

    /**
     * Valider l'unicité du nom de vaccin
     */
    private void validateVaccineName(String name) {
        if (vaccineRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Un vaccin avec ce nom existe déjà: " + name);
        }
    }
}