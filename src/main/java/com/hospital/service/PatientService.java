package com.hospital.service;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Créer un nouveau patient
     */
    public Patient createPatient(Patient patient) {
        log.info("Création d'un nouveau patient: {} {}", patient.getFirstName(), patient.getLastName());
        
        // Générer un numéro patient unique si non fourni
        if (patient.getPatientNumber() == null || patient.getPatientNumber().isEmpty()) {
            patient.setPatientNumber(generatePatientNumber());
        }
        
        // Vérifier l'unicité du numéro patient
        validatePatientNumber(patient.getPatientNumber());
        
        // Vérifier l'unicité de l'email si fourni
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            validateEmail(patient.getEmail());
        }
        
        patient.setRegistrationDate(LocalDateTime.now());
        patient.setIsActive(true);
        
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient créé avec succès avec l'ID: {} et numéro: {}", 
                savedPatient.getId(), savedPatient.getPatientNumber());
        
        return savedPatient;
    }

    /**
     * Mettre à jour un patient existant
     */
    public Patient updatePatient(Long id, Patient patient) {
        log.info("Mise à jour du patient avec l'ID: {}", id);
        
        Patient existingPatient = getPatientById(id);
        
        // Mise à jour des champs
        existingPatient.setFirstName(patient.getFirstName());
        existingPatient.setLastName(patient.getLastName());
        existingPatient.setDateOfBirth(patient.getDateOfBirth());
        existingPatient.setGender(patient.getGender());
        existingPatient.setPhone(patient.getPhone());
        existingPatient.setEmail(patient.getEmail());
        existingPatient.setAddress(patient.getAddress());
        existingPatient.setCity(patient.getCity());
        existingPatient.setPostalCode(patient.getPostalCode());
        existingPatient.setCountry(patient.getCountry());
        existingPatient.setNationalId(patient.getNationalId());
        existingPatient.setSocialSecurityNumber(patient.getSocialSecurityNumber());
        existingPatient.setEmergencyContactName(patient.getEmergencyContactName());
        existingPatient.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        existingPatient.setEmergencyContactRelation(patient.getEmergencyContactRelation());
        existingPatient.setBloodType(patient.getBloodType());
        existingPatient.setAllergies(patient.getAllergies());
        existingPatient.setMedicalHistory(patient.getMedicalHistory());
        existingPatient.setCurrentMedications(patient.getCurrentMedications());
        existingPatient.setInsuranceNumber(patient.getInsuranceNumber());
        existingPatient.setInsuranceProvider(patient.getInsuranceProvider());
        existingPatient.setPreferredLanguage(patient.getPreferredLanguage());
        existingPatient.setMaritalStatus(patient.getMaritalStatus());
        existingPatient.setOccupation(patient.getOccupation());
        existingPatient.setNotes(patient.getNotes());
        
        return patientRepository.save(existingPatient);
    }

    /**
     * Obtenir un patient par ID
     */
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir un patient par numéro
     */
    @Transactional(readOnly = true)
    public Patient getPatientByNumber(String patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec le numéro: " + patientNumber));
    }

    /**
     * Obtenir tous les patients avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    /**
     * Rechercher des patients
     */
    @Transactional(readOnly = true)
    public Page<Patient> searchPatients(String searchTerm, Pageable pageable) {
        return patientRepository.searchPatients(searchTerm, pageable);
    }

    /**
     * Obtenir les patients actifs
     */
    @Transactional(readOnly = true)
    public List<Patient> getActivePatients() {
        return patientRepository.findActivePatients();
    }

    /**
     * Obtenir les patients par genre
     */
    @Transactional(readOnly = true)
    public List<Patient> getPatientsByGender(Patient.Gender gender) {
        return patientRepository.findByGender(gender);
    }

    /**
     * Obtenir les patients par groupe sanguin
     */
    @Transactional(readOnly = true)
    public List<Patient> getPatientsByBloodType(Patient.BloodType bloodType) {
        return patientRepository.findByBloodType(bloodType);
    }

    /**
     * Obtenir les patients par ville
     */
    @Transactional(readOnly = true)
    public List<Patient> getPatientsByCity(String city) {
        return patientRepository.findByCity(city);
    }

    /**
     * Obtenir les patients enregistrés récemment
     */
    @Transactional(readOnly = true)
    public List<Patient> getRecentlyRegisteredPatients(Pageable pageable) {
        return patientRepository.findRecentlyRegisteredPatients(pageable);
    }

    /**
     * Obtenir les statistiques des patients par genre
     */
    @Transactional(readOnly = true)
    public List<Object[]> getPatientStatsByGender() {
        return patientRepository.getPatientCountByGender();
    }

    /**
     * Obtenir les statistiques des patients par groupe sanguin
     */
    @Transactional(readOnly = true)
    public List<Object[]> getPatientStatsByBloodType() {
        return patientRepository.getPatientCountByBloodType();
    }

    /**
     * Désactiver un patient
     */
    public void deactivatePatient(Long id) {
        log.info("Désactivation du patient avec l'ID: {}", id);
        Patient patient = getPatientById(id);
        patient.setIsActive(false);
        patientRepository.save(patient);
    }

    /**
     * Réactiver un patient
     */
    public void reactivatePatient(Long id) {
        log.info("Réactivation du patient avec l'ID: {}", id);
        Patient patient = getPatientById(id);
        patient.setIsActive(true);
        patientRepository.save(patient);
    }

    /**
     * Supprimer un patient
     */
    public void deletePatient(Long id) {
        log.info("Suppression du patient avec l'ID: {}", id);
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }

    /**
     * Compter les patients actifs
     */
    @Transactional(readOnly = true)
    public long countActivePatients() {
        return patientRepository.countActivePatients();
    }

    /**
     * Compter les patients enregistrés depuis une date
     */
    @Transactional(readOnly = true)
    public long countPatientsRegisteredSince(LocalDateTime date) {
        return patientRepository.countPatientsRegisteredSince(date);
    }

    /**
     * Mettre à jour la date de dernière visite
     */
    public void updateLastVisitDate(Long patientId) {
        Patient patient = getPatientById(patientId);
        patient.setLastVisitDate(LocalDateTime.now());
        patientRepository.save(patient);
    }

    /**
     * Générer un numéro patient unique
     */
    private String generatePatientNumber() {
        String prefix = "P";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp.substring(timestamp.length() - 8) + suffix;
    }

    /**
     * Valider l'unicité du numéro patient
     */
    private void validatePatientNumber(String patientNumber) {
        Optional<Patient> existingPatient = patientRepository.findByPatientNumber(patientNumber);
        if (existingPatient.isPresent()) {
            throw new RuntimeException("Un patient avec ce numéro existe déjà: " + patientNumber);
        }
    }

    /**
     * Valider l'unicité de l'email
     */
    private void validateEmail(String email) {
        Optional<Patient> existingPatient = patientRepository.findByEmail(email);
        if (existingPatient.isPresent()) {
            throw new RuntimeException("Un patient avec cet email existe déjà: " + email);
        }
    }
}