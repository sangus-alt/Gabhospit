package com.hospital.service;

import com.hospital.entity.Medication;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.repository.PharmacyRepository;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.DoctorRepository;
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
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // Gestion des médicaments
    public Medication createMedication(Medication medication) {
        log.info("Création d'un nouveau médicament: {}", medication.getBrandName());
        
        medication.setMedicationCode(generateMedicationCode());
        medication.setIsActive(true);
        medication.setCreatedDate(LocalDateTime.now());
        
        Medication savedMedication = pharmacyRepository.save(medication);
        log.info("Médicament créé avec le code: {}", savedMedication.getMedicationCode());
        return savedMedication;
    }

    @Transactional(readOnly = true)
    public Medication getMedicationById(Long id) {
        return pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médicament non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Medication getMedicationByBrandName(String brandName) {
        return pharmacyRepository.findByBrandName(brandName)
                .orElseThrow(() -> new RuntimeException("Médicament non trouvé avec le nom: " + brandName));
    }

    @Transactional(readOnly = true)
    public Page<Medication> getAllMedications(Pageable pageable) {
        return pharmacyRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Medication> getActiveMedications() {
        return pharmacyRepository.findByIsActive(true);
    }

    @Transactional(readOnly = true)
    public List<Medication> getMedicationsByCategory(String category) {
        return pharmacyRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<Medication> getMedicationsByManufacturer(String manufacturer) {
        return pharmacyRepository.findByManufacturer(manufacturer);
    }

    @Transactional(readOnly = true)
    public Page<Medication> searchMedications(String searchTerm, Pageable pageable) {
        return pharmacyRepository.searchMedications(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Medication> getLowStockMedications() {
        return pharmacyRepository.findLowStockMedications();
    }

    @Transactional(readOnly = true)
    public List<Medication> getExpiredMedications() {
        return pharmacyRepository.findExpiredMedications(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Medication> getMedicationsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return pharmacyRepository.findMedicationsExpiringBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Medication> getMedicationsWithStockAlerts() {
        LocalDate alertDate = LocalDate.now().plusDays(30); // Alerte 30 jours avant expiration
        return pharmacyRepository.findMedicationsWithStockAlerts(alertDate);
    }

    @Transactional(readOnly = true)
    public Double getTotalStockValue() {
        return pharmacyRepository.getTotalStockValue();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMostPrescribedMedications(Pageable pageable) {
        return pharmacyRepository.findMostPrescribedMedications(pageable);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMedicationStatsByCategory() {
        return pharmacyRepository.countMedicationsByCategory();
    }

    public Medication updateMedication(Long id, Medication medicationDetails) {
        Medication medication = getMedicationById(id);
        
        medication.setBrandName(medicationDetails.getBrandName());
        medication.setGenericName(medicationDetails.getGenericName());
        medication.setManufacturer(medicationDetails.getManufacturer());
        medication.setCategory(medicationDetails.getCategory());
        medication.setPrice(medicationDetails.getPrice());
        medication.setCurrentStock(medicationDetails.getCurrentStock());
        medication.setMinimumStock(medicationDetails.getMinimumStock());
        medication.setExpiryDate(medicationDetails.getExpiryDate());
        medication.setDosage(medicationDetails.getDosage());
        medication.setDescription(medicationDetails.getDescription());
        medication.setSideEffects(medicationDetails.getSideEffects());
        medication.setContraindications(medicationDetails.getContraindications());
        medication.setRequiresPrescription(medicationDetails.getRequiresPrescription());
        
        return pharmacyRepository.save(medication);
    }

    public Medication updateStock(Long medicationId, Integer newStock) {
        Medication medication = getMedicationById(medicationId);
        medication.setCurrentStock(newStock);
        medication.setLastUpdated(LocalDateTime.now());
        return pharmacyRepository.save(medication);
    }

    public Medication reduceStock(Long medicationId, Integer quantity) {
        Medication medication = getMedicationById(medicationId);
        
        if (medication.getCurrentStock() < quantity) {
            throw new RuntimeException("Stock insuffisant pour le médicament: " + medication.getBrandName());
        }
        
        medication.setCurrentStock(medication.getCurrentStock() - quantity);
        medication.setLastUpdated(LocalDateTime.now());
        
        // Log si le stock devient faible
        if (medication.getCurrentStock() <= medication.getMinimumStock()) {
            log.warn("Stock faible pour le médicament {}: {} unités restantes", 
                    medication.getBrandName(), medication.getCurrentStock());
        }
        
        return pharmacyRepository.save(medication);
    }

    public Medication addStock(Long medicationId, Integer quantity) {
        Medication medication = getMedicationById(medicationId);
        medication.setCurrentStock(medication.getCurrentStock() + quantity);
        medication.setLastUpdated(LocalDateTime.now());
        return pharmacyRepository.save(medication);
    }

    // Gestion des prescriptions
    public Prescription createPrescription(Prescription prescription) {
        log.info("Création d'une nouvelle prescription pour le patient ID: {}", prescription.getPatient().getId());
        
        prescription.setPrescriptionNumber(generatePrescriptionNumber());
        prescription.setPrescriptionDate(LocalDateTime.now());
        prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
        
        // Calculer la date de validité (par défaut 30 jours)
        if (prescription.getValidUntil() == null) {
            prescription.setValidUntil(LocalDate.now().plusDays(30));
        }
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription créée avec le numéro: {}", savedPrescription.getPrescriptionNumber());
        return savedPrescription;
    }

    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription non trouvée avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + patientId));
        return prescriptionRepository.findByPatientOrderByPrescriptionDateDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return prescriptionRepository.findByDoctorOrderByPrescriptionDate(doctor);
    }

    @Transactional(readOnly = true)
    public List<Prescription> getPendingPrescriptions() {
        return prescriptionRepository.findPendingPrescriptions();
    }

    @Transactional(readOnly = true)
    public List<Prescription> getActivePrescriptions() {
        return prescriptionRepository.findActivePrescriptions();
    }

    @Transactional(readOnly = true)
    public List<Prescription> getExpiredPrescriptions() {
        return prescriptionRepository.findExpiredPrescriptions();
    }

    @Transactional(readOnly = true)
    public List<Prescription> getTodayDispensedPrescriptions() {
        return prescriptionRepository.findTodayDispensedPrescriptions();
    }

    public Prescription dispensePrescription(Long prescriptionId, Long pharmacistId) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        
        if (!"PENDING".equals(prescription.getStatus())) {
            throw new RuntimeException("Cette prescription ne peut pas être dispensée");
        }
        
        // Vérifier la validité
        if (prescription.getValidUntil().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cette prescription a expiré");
        }
        
        // Vérifier et réduire le stock pour chaque médicament
        for (PrescriptionItem item : prescription.getItems()) {
            reduceStock(item.getMedication().getId(), item.getQuantity());
        }
        
        prescription.setStatus(Prescription.PrescriptionStatus.COMPLETED);
        prescription.setDispensedDate(LocalDateTime.now());
        prescription.setDispensedBy(pharmacistId);
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription dispensée: {}", prescription.getPrescriptionNumber());
        return savedPrescription;
    }

    public boolean checkDrugInteractions(List<Long> medicationIds) {
        // Logique simplifiée pour vérifier les interactions médicamenteuses
        // Dans un vrai système, cela ferait appel à une base de données d'interactions
        for (int i = 0; i < medicationIds.size(); i++) {
            for (int j = i + 1; j < medicationIds.size(); j++) {
                // Vérification des interactions entre médicaments
                if (hasInteraction(medicationIds.get(i), medicationIds.get(j))) {
                    return true; // Interaction détectée
                }
            }
        }
        return false; // Aucune interaction
    }

    private boolean hasInteraction(Long medicationId1, Long medicationId2) {
        // Logique simplifiée - dans un vrai système, utiliser une base de données d'interactions
        return false;
    }

    public void deactivateMedication(Long id) {
        Medication medication = getMedicationById(id);
        medication.setIsActive(false);
        pharmacyRepository.save(medication);
        log.info("Médicament désactivé: {}", medication.getBrandName());
    }

    private String generateMedicationCode() {
        return "MED" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String generatePrescriptionNumber() {
        return "RX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Transactional(readOnly = true)
    public Long countActiveMedications() {
        return (long) pharmacyRepository.findByIsActive(true).size();
    }

    @Transactional(readOnly = true)
    public Long countLowStockMedications() {
        return (long) pharmacyRepository.findLowStockMedications().size();
    }

    @Transactional(readOnly = true)
    public Long countPendingPrescriptions() {
        return (long) prescriptionRepository.findPendingPrescriptions().size();
    }

    @Transactional(readOnly = true)
    public Long countTodayDispensedPrescriptions() {
        return (long) prescriptionRepository.findTodayDispensedPrescriptions().size();
    }
}