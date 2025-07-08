package com.hospital.repository;

import com.hospital.entity.Doctor;
import com.hospital.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Recherche par numéro de médecin unique
    Optional<Doctor> findByDoctorNumber(String doctorNumber);

    // Recherche par nom ou prénom
    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Doctor> searchDoctors(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Recherche par département
    List<Doctor> findByDepartment(Department department);

    // Recherche par spécialisation
    List<Doctor> findBySpecialization(String specialization);

    // Médecins actifs
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true")
    List<Doctor> findActiveDoctor();

    // Médecins disponibles aujourd'hui
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true AND d.isAvailable = true")
    List<Doctor> findAvailableDoctors();

    // Compter les médecins par spécialisation
    @Query("SELECT d.specialization, COUNT(d) FROM Doctor d GROUP BY d.specialization")
    List<Object[]> countDoctorsBySpecialization();

    // Compter les médecins actifs
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isActive = true")
    Long countActiveDoctors();

    // Recherche par statut
    List<Doctor> findByIsActive(Boolean isActive);

    // Recherche par disponibilité
    List<Doctor> findByIsAvailable(Boolean isAvailable);

    // Médecins avec consultations aujourd'hui
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.consultations c WHERE DATE(c.consultationDate) = CURRENT_DATE")
    List<Doctor> findDoctorsWithTodayConsultations();
}