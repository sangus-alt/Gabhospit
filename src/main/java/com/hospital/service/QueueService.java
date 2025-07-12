package com.hospital.service;

import com.hospital.entity.QueueManagement;
import com.hospital.entity.Patient;
import com.hospital.entity.Department;
import com.hospital.repository.QueueRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.DepartmentRepository;
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
public class QueueService {

    private final QueueRepository queueRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;

    public QueueManagement addPatientToQueue(Long patientId, Long departmentId, Integer priority, String notes) {
        log.info("Ajout du patient ID: {} à la file d'attente du département ID: {}", patientId, departmentId);
        
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + patientId));
        
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departmentId));
        
        // Vérifier si le patient n'est pas déjà en file d'attente
        if (queueRepository.findByPatientAndStatus(patient, "WAITING").isPresent()) {
            throw new RuntimeException("Le patient est déjà en file d'attente");
        }
        
        QueueManagement queueItem = new QueueManagement();
        queueItem.setPatient(patient);
        queueItem.setDepartment(department);
        queueItem.setQueueNumber(generateQueueNumber(department));
        queueItem.setPriority(priority != null ? QueueManagement.Priority.values()[priority - 1] : calculatePriority(patient));
        queueItem.setArrivalTime(LocalDateTime.now());
        queueItem.setStatus(QueueManagement.QueueStatus.WAITING);
        queueItem.setNotes(notes);
        
        // Calculer l'estimation du temps d'attente
        queueItem.setEstimatedWaitTime(calculateEstimatedWaitTime(department, queueItem.getPriority()));
        
        QueueManagement savedQueue = queueRepository.save(queueItem);
        log.info("Patient ajouté à la file d'attente avec le numéro: {}", savedQueue.getQueueNumber());
        return savedQueue;
    }

    @Transactional(readOnly = true)
    public QueueManagement getQueueItemById(Long id) {
        return queueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Élément de file d'attente non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getActiveQueue() {
        return queueRepository.findActiveQueue();
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getQueueByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departmentId));
        return queueRepository.findQueueByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getTodayQueue() {
        return queueRepository.findTodayQueue();
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getQueueByStatus(String status) {
        return queueRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getNextPatients(int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        return queueRepository.findNextPatients(pageable);
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getPatientsInProgress() {
        return queueRepository.findPatientsInProgress();
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getHighPriorityPatients(Integer minPriority) {
        return queueRepository.findHighPriorityPatients(minPriority);
    }

    @Transactional(readOnly = true)
    public List<QueueManagement> getLongWaitingPatients(Integer maxWaitMinutes) {
        return queueRepository.findLongWaitingPatients(maxWaitMinutes);
    }

    @Transactional(readOnly = true)
    public Integer getQueuePosition(Long queueId) {
        QueueManagement queueItem = getQueueItemById(queueId);
        return queueRepository.getQueuePosition(queueItem.getDepartment(), 
                                               queueItem.getPriority(), 
                                               queueItem.getArrivalTime());
    }

    @Transactional(readOnly = true)
    public Double getAverageWaitingTime() {
        return queueRepository.getAverageWaitingTime();
    }

    @Transactional(readOnly = true)
    public Double getAverageWaitingTimeByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departmentId));
        return queueRepository.getAverageWaitingTimeByDepartment(department);
    }

    @Transactional(readOnly = true)
    public Page<QueueManagement> searchQueue(String searchTerm, Pageable pageable) {
        return queueRepository.searchQueue(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTodayQueueStatsByDepartment() {
        return queueRepository.getTodayQueueStatsByDepartment();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getQueueStatsByStatus() {
        return queueRepository.countTodayQueueByStatus();
    }

    public QueueManagement callNextPatient(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departmentId));
        
        List<QueueManagement> waitingPatients = queueRepository.findQueueByDepartment(department);
        
        if (waitingPatients.isEmpty()) {
            throw new RuntimeException("Aucun patient en attente dans ce département");
        }
        
        QueueManagement nextPatient = waitingPatients.get(0); // Premier de la liste (trié par priorité et heure)
        nextPatient.setStatus(QueueManagement.QueueStatus.CALLED);
        nextPatient.setCalledTime(LocalDateTime.now());
        
        QueueManagement savedQueue = queueRepository.save(nextPatient);
        log.info("Patient appelé: {} - Numéro: {}", 
                nextPatient.getPatient().getFirstName() + " " + nextPatient.getPatient().getLastName(),
                nextPatient.getQueueNumber());
        return savedQueue;
    }

    public QueueManagement startService(Long queueId) {
        QueueManagement queueItem = getQueueItemById(queueId);
        queueItem.setStatus(QueueManagement.QueueStatus.IN_SERVICE);
        queueItem.setServiceStartTime(LocalDateTime.now());
        return queueRepository.save(queueItem);
    }

    public QueueManagement completeService(Long queueId, String serviceNotes) {
        QueueManagement queueItem = getQueueItemById(queueId);
        queueItem.setStatus(QueueManagement.QueueStatus.COMPLETED);
        queueItem.setCompletionTime(LocalDateTime.now());
        queueItem.setServiceNotes(serviceNotes);
        
        QueueManagement savedQueue = queueRepository.save(queueItem);
        log.info("Service terminé pour le patient: {}", queueItem.getQueueNumber());
        return savedQueue;
    }

    public QueueManagement markAsNoShow(Long queueId) {
        QueueManagement queueItem = getQueueItemById(queueId);
        queueItem.setStatus(QueueManagement.QueueStatus.NO_SHOW);
        queueItem.setCompletionTime(LocalDateTime.now());
        return queueRepository.save(queueItem);
    }

    public QueueManagement updatePriority(Long queueId, Integer newPriority) {
        QueueManagement queueItem = getQueueItemById(queueId);
        queueItem.setPriority(QueueManagement.Priority.values()[newPriority - 1]);
        
        // Recalculer l'estimation du temps d'attente
        queueItem.setEstimatedWaitTime(calculateEstimatedWaitTime(queueItem.getDepartment(), newPriority));
        
        return queueRepository.save(queueItem);
    }

    public void removeFromQueue(Long queueId, String reason) {
        QueueManagement queueItem = getQueueItemById(queueId);
        queueItem.setStatus(QueueManagement.QueueStatus.CANCELLED);
        queueItem.setCompletionTime(LocalDateTime.now());
        queueItem.setNotes(queueItem.getNotes() + " - Annulé: " + reason);
        queueRepository.save(queueItem);
        log.info("Patient retiré de la file d'attente: {} - Raison: {}", queueItem.getQueueNumber(), reason);
    }

    private String generateQueueNumber(Department department) {
        String prefix = department.getCode() != null ? department.getCode() : "Q";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        return prefix + timestamp + String.format("%03d", (int)(Math.random() * 1000));
    }

    private QueueManagement.Priority calculatePriority(Patient patient) {
        // Logique de calcul de priorité basée sur l'âge, l'urgence, etc.
        QueueManagement.Priority priority = QueueManagement.Priority.MEDIUM; // Priorité normale par défaut
        
        // Priorité plus élevée pour les personnes âgées (65+)
        if (patient.getDateOfBirth() != null) {
            int age = LocalDateTime.now().getYear() - patient.getDateOfBirth().getYear();
            if (age >= 65) {
                priority = QueueManagement.Priority.HIGH;
            } else if (age <= 5) {
                priority = QueueManagement.Priority.HIGH; // Priorité pour les jeunes enfants
            }
        }
        
        // Autres critères de priorité peuvent être ajoutés ici
        return priority;
    }

    private Integer calculateEstimatedWaitTime(Department department, Integer priority) {
        // Calcul basé sur le nombre de patients en attente et la priorité
        List<QueueManagement> waitingPatients = queueRepository.findQueueByDepartment(department);
        
        int patientsAhead = 0;
        for (QueueManagement patient : waitingPatients) {
            if (patient.getPriority().ordinal() > priority || 
                (patient.getPriority().equals(priority) && patient.getArrivalTime().isBefore(LocalDateTime.now()))) {
                patientsAhead++;
            }
        }
        
        // Estimation: 15 minutes par patient en moyenne
        return patientsAhead * 15;
    }

    @Transactional(readOnly = true)
    public Long countWaitingPatients() {
        return (long) queueRepository.findByStatus("WAITING").size();
    }

    @Transactional(readOnly = true)
    public Long countTodayQueue() {
        return (long) queueRepository.findTodayQueue().size();
    }

    @Transactional(readOnly = true)
    public Long countPatientsInProgress() {
        return (long) queueRepository.findPatientsInProgress().size();
    }
}