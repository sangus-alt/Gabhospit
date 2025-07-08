package com.hospital.service;

import com.hospital.entity.LabTest;
import com.hospital.entity.LabOrder;
import com.hospital.entity.LabResult;
import com.hospital.entity.Patient;
import com.hospital.entity.Doctor;
import com.hospital.repository.LabTestRepository;
import com.hospital.repository.LabOrderRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LaboratoryService {

    private final LabTestRepository labTestRepository;
    private final LabOrderRepository labOrderRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // Gestion des tests de laboratoire
    public LabTest createLabTest(LabTest labTest) {
        log.info("Création d'un nouveau test de laboratoire: {}", labTest.getTestName());
        
        labTest.setTestCode(generateTestCode());
        labTest.setIsActive(true);
        labTest.setCreatedDate(LocalDateTime.now());
        
        LabTest savedTest = labTestRepository.save(labTest);
        log.info("Test de laboratoire créé avec le code: {}", savedTest.getTestCode());
        return savedTest;
    }

    @Transactional(readOnly = true)
    public LabTest getLabTestById(Long id) {
        return labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test de laboratoire non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public LabTest getLabTestByCode(String testCode) {
        return labTestRepository.findByTestCode(testCode)
                .orElseThrow(() -> new RuntimeException("Test non trouvé avec le code: " + testCode));
    }

    @Transactional(readOnly = true)
    public Page<LabTest> getAllLabTests(Pageable pageable) {
        return labTestRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<LabTest> getActiveLabTests() {
        return labTestRepository.findByIsActive(true);
    }

    @Transactional(readOnly = true)
    public List<LabTest> getLabTestsByCategory(String category) {
        return labTestRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<LabTest> getLabTestsBySampleType(String sampleType) {
        return labTestRepository.findBySampleType(sampleType);
    }

    @Transactional(readOnly = true)
    public Page<LabTest> searchLabTests(String searchTerm, Pageable pageable) {
        return labTestRepository.searchLabTests(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<LabTest> getUrgentAvailableTests() {
        return labTestRepository.findUrgentAvailableTests();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMostOrderedTests(Pageable pageable) {
        return labTestRepository.findMostOrderedTests(pageable);
    }

    // Gestion des commandes de laboratoire
    public LabOrder createLabOrder(LabOrder labOrder) {
        log.info("Création d'une nouvelle commande de laboratoire pour le patient ID: {}", 
                labOrder.getPatient().getId());
        
        labOrder.setSampleId(generateSampleId());
        labOrder.setOrderDate(LocalDateTime.now());
        labOrder.setStatus("PENDING");
        
        LabOrder savedOrder = labOrderRepository.save(labOrder);
        log.info("Commande de laboratoire créée avec l'ID échantillon: {}", savedOrder.getSampleId());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public LabOrder getLabOrderById(Long id) {
        return labOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande de laboratoire non trouvée avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getLabOrdersByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + patientId));
        return labOrderRepository.findByPatientOrderByOrderDateDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getLabOrdersByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + doctorId));
        return labOrderRepository.findByDoctorOrderByOrderDate(doctor);
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getLabOrdersByStatus(String status) {
        return labOrderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getTodayLabOrders() {
        return labOrderRepository.findTodayOrders();
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getPendingLabOrders() {
        return labOrderRepository.findPendingOrders();
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getReadyForCollectionOrders() {
        return labOrderRepository.findReadyForCollection();
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getUrgentOrders() {
        return labOrderRepository.findByIsUrgent(true);
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getDelayedOrders() {
        return labOrderRepository.findDelayedOrders();
    }

    public LabOrder updateOrderStatus(Long orderId, String status) {
        LabOrder order = getLabOrderById(orderId);
        order.setStatus(status);
        
        if ("COLLECTED".equals(status)) {
            order.setCollectionDate(LocalDateTime.now());
        } else if ("COMPLETED".equals(status)) {
            order.setCompletionDate(LocalDateTime.now());
        }
        
        return labOrderRepository.save(order);
    }

    public LabOrder collectSample(Long orderId, String collectedBy) {
        LabOrder order = getLabOrderById(orderId);
        order.setStatus("COLLECTED");
        order.setCollectionDate(LocalDateTime.now());
        order.setCollectedBy(collectedBy);
        
        LabOrder savedOrder = labOrderRepository.save(order);
        log.info("Échantillon collecté pour la commande ID: {}", orderId);
        return savedOrder;
    }

    public LabOrder startProcessing(Long orderId) {
        LabOrder order = getLabOrderById(orderId);
        order.setStatus("PROCESSING");
        order.setProcessingStartDate(LocalDateTime.now());
        
        return labOrderRepository.save(order);
    }

    public LabOrder completeOrder(Long orderId, String results, String notes) {
        LabOrder order = getLabOrderById(orderId);
        order.setStatus("COMPLETED");
        order.setCompletionDate(LocalDateTime.now());
        order.setResults(results);
        order.setNotes(notes);
        
        LabOrder savedOrder = labOrderRepository.save(order);
        log.info("Commande de laboratoire terminée ID: {}", orderId);
        return savedOrder;
    }

    public LabOrder markAsUrgent(Long orderId) {
        LabOrder order = getLabOrderById(orderId);
        order.setIsUrgent(true);
        return labOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Double getAverageWaitingTime() {
        return labOrderRepository.getAverageWaitingTime();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getOrderStatsByStatus() {
        return labOrderRepository.countOrdersByStatus();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTestStatsByCategory() {
        return labTestRepository.countTestsByCategory();
    }

    public LabTest updateLabTest(Long id, LabTest testDetails) {
        LabTest labTest = getLabTestById(id);
        
        labTest.setTestName(testDetails.getTestName());
        labTest.setDescription(testDetails.getDescription());
        labTest.setCategory(testDetails.getCategory());
        labTest.setSampleType(testDetails.getSampleType());
        labTest.setPrice(testDetails.getPrice());
        labTest.setProcessingTimeHours(testDetails.getProcessingTimeHours());
        labTest.setPreparationInstructions(testDetails.getPreparationInstructions());
        labTest.setNormalRanges(testDetails.getNormalRanges());
        
        return labTestRepository.save(labTest);
    }

    public void deactivateLabTest(Long id) {
        LabTest labTest = getLabTestById(id);
        labTest.setIsActive(false);
        labTestRepository.save(labTest);
        log.info("Test de laboratoire désactivé: {}", labTest.getTestCode());
    }

    private String generateTestCode() {
        return "LT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String generateSampleId() {
        return "S" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Transactional(readOnly = true)
    public Long countTodayOrders() {
        return (long) labOrderRepository.findTodayOrders().size();
    }

    @Transactional(readOnly = true)
    public Long countPendingOrders() {
        return (long) labOrderRepository.findPendingOrders().size();
    }

    @Transactional(readOnly = true)
    public Long countCompletedOrdersToday() {
        return (long) labOrderRepository.findByStatus("COMPLETED").size();
    }
}