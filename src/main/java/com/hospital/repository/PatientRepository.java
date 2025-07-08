package com.hospital.repository;

import com.hospital.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientNumber(String patientNumber);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByNationalId(String nationalId);

    Optional<Patient> findBySocialSecurityNumber(String socialSecurityNumber);

    List<Patient> findByFirstNameIgnoreCase(String firstName);

    List<Patient> findByLastNameIgnoreCase(String lastName);

    List<Patient> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

    List<Patient> findByGender(Patient.Gender gender);

    List<Patient> findByDateOfBirth(LocalDate dateOfBirth);

    List<Patient> findByBloodType(Patient.BloodType bloodType);

    List<Patient> findByCity(String city);

    List<Patient> findByCountry(String country);

    List<Patient> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Patient> findByLastVisitDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Patient> findByInsuranceProvider(String insuranceProvider);

    @Query("SELECT p FROM Patient p WHERE p.isActive = true")
    List<Patient> findActivePatients();

    @Query("SELECT p FROM Patient p WHERE p.isActive = false")
    List<Patient> findInactivePatients();

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.patientNumber LIKE CONCAT('%', :searchTerm, '%') OR " +
           "p.phone LIKE CONCAT('%', :searchTerm, '%')")
    Page<Patient> searchPatients(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Patient> findByDateOfBirthRange(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Patient p WHERE TIMESTAMPDIFF(YEAR, p.dateOfBirth, CURDATE()) BETWEEN :minAge AND :maxAge")
    List<Patient> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.registrationDate >= :date")
    long countPatientsRegisteredSince(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.isActive = true")
    long countActivePatients();

    @Query("SELECT p FROM Patient p WHERE p.allergies IS NOT NULL AND p.allergies != ''")
    List<Patient> findPatientsWithAllergies();

    @Query("SELECT p FROM Patient p WHERE p.emergencyContactName IS NULL OR p.emergencyContactPhone IS NULL")
    List<Patient> findPatientsWithoutEmergencyContact();

    @Query("SELECT p FROM Patient p WHERE p.lastVisitDate IS NULL OR p.lastVisitDate < :date")
    List<Patient> findPatientsNotVisitedSince(@Param("date") LocalDateTime date);

    @Query("SELECT p FROM Patient p ORDER BY p.registrationDate DESC")
    List<Patient> findRecentlyRegisteredPatients(Pageable pageable);

    @Query("SELECT p.gender, COUNT(p) FROM Patient p GROUP BY p.gender")
    List<Object[]> getPatientCountByGender();

    @Query("SELECT p.bloodType, COUNT(p) FROM Patient p WHERE p.bloodType IS NOT NULL GROUP BY p.bloodType")
    List<Object[]> getPatientCountByBloodType();

    @Query("SELECT p.city, COUNT(p) FROM Patient p WHERE p.city IS NOT NULL GROUP BY p.city")
    List<Object[]> getPatientCountByCity();

    // Méthode pour vérifier l'existence d'un patient par nom, prénom et date de naissance
    boolean existsByFirstNameAndLastNameAndDateOfBirth(String firstName, String lastName, LocalDate dateOfBirth);
}