package com.hospital.service;

import com.hospital.entity.Doctor;
import com.hospital.entity.Department;
import com.hospital.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public Doctor createDoctor(Doctor doctor) {
        log.info("Création d'un nouveau médecin: {} {}", doctor.getFirstName(), doctor.getLastName());
        
        // Générer un numéro de médecin unique
        doctor.setDoctorNumber(generateDoctorNumber());
        doctor.setIsActive(true);
        doctor.setIsAvailable(true);
        doctor.setHireDate(LocalDateTime.now());
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Médecin créé avec succès avec le numéro: {}", savedDoctor.getDoctorNumber());
        return savedDoctor;
    }

    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Doctor getDoctorByNumber(String doctorNumber) {
        return doctorRepository.findByDoctorNumber(doctorNumber)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec le numéro: " + doctorNumber));
    }

    @Transactional(readOnly = true)
    public Page<Doctor> getAllDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Doctor> searchDoctors(String searchTerm, Pageable pageable) {
        return doctorRepository.searchDoctors(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findActiveDoctor();
    }

    @Transactional(readOnly = true)
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findAvailableDoctors();
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departmentId));
        return doctorRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsWithTodayConsultations() {
        return doctorRepository.findDoctorsWithTodayConsultations();
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = getDoctorById(id);
        
        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setEmail(doctorDetails.getEmail());
        doctor.setPhone(doctorDetails.getPhone());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setDepartment(doctorDetails.getDepartment());
        doctor.setLicenseNumber(doctorDetails.getLicenseNumber());
        doctor.setQualifications(doctorDetails.getQualifications());
        doctor.setExperienceYears(doctorDetails.getExperienceYears());
        doctor.setConsultationFee(doctorDetails.getConsultationFee());
        doctor.setWorkingHours(doctorDetails.getWorkingHours());
        doctor.setBio(doctorDetails.getBio());
        
        return doctorRepository.save(doctor);
    }

    public void setDoctorAvailability(Long id, Boolean isAvailable) {
        Doctor doctor = getDoctorById(id);
        doctor.setIsAvailable(isAvailable);
        doctorRepository.save(doctor);
        log.info("Disponibilité du médecin {} mise à jour: {}", doctor.getDoctorNumber(), isAvailable);
    }

    public void deactivateDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctor.setIsActive(false);
        doctor.setIsAvailable(false);
        doctorRepository.save(doctor);
        log.info("Médecin désactivé: {}", doctor.getDoctorNumber());
    }

    public void reactivateDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctor.setIsActive(true);
        doctor.setIsAvailable(true);
        doctorRepository.save(doctor);
        log.info("Médecin réactivé: {}", doctor.getDoctorNumber());
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
        log.info("Médecin supprimé: {}", doctor.getDoctorNumber());
    }

    @Transactional(readOnly = true)
    public Long countActiveDoctors() {
        return doctorRepository.countActiveDoctors();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getDoctorStatsBySpecialization() {
        return doctorRepository.countDoctorsBySpecialization();
    }

    private String generateDoctorNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "D" + timestamp.substring(timestamp.length() - 8) + uuid;
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsByAvailability(Boolean isAvailable) {
        return doctorRepository.findByIsAvailable(isAvailable);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsByStatus(Boolean isActive) {
        return doctorRepository.findByIsActive(isActive);
    }

    public Doctor assignDoctorToDepartment(Long doctorId, Long departmentId) {
        Doctor doctor = getDoctorById(doctorId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departmentId));
        
        doctor.setDepartment(department);
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctorConsultationFee(Long id, Double newFee) {
        Doctor doctor = getDoctorById(id);
        doctor.setConsultationFee(newFee);
        return doctorRepository.save(doctor);
    }
}