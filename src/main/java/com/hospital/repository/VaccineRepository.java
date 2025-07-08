package com.hospital.repository;

import com.hospital.entity.Vaccine;
import com.hospital.entity.VaccinationRecord;
import com.hospital.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

    // Recherche par nom de vaccin
    Optional<Vaccine> findByVaccineName(String vaccineName);

    // Recherche par code de vaccin
    Optional<Vaccine> findByVaccineCode(String vaccineCode);

    // Vaccins actifs
    List<Vaccine> findByIsActive(Boolean isActive);

    // Vaccins par type
    List<Vaccine> findByVaccineType(String vaccineType);

    // Recherche dans les vaccins
    @Query("SELECT v FROM Vaccine v WHERE " +
           "LOWER(v.vaccineName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.manufacturer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.vaccineCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Vaccine> searchVaccines(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Vaccins par fabricant
    List<Vaccine> findByManufacturer(String manufacturer);

    // Stock faible de vaccins
    @Query("SELECT v FROM Vaccine v WHERE v.currentStock <= v.minimumStock")
    List<Vaccine> findLowStockVaccines();

    // Vaccins périmés
    @Query("SELECT v FROM Vaccine v WHERE v.expiryDate <= :date")
    List<Vaccine> findExpiredVaccines(@Param("date") LocalDate date);

    // Vaccins à expirer bientôt
    @Query("SELECT v FROM Vaccine v WHERE v.expiryDate BETWEEN :today AND :futureDate")
    List<Vaccine> findVaccinesExpiringBetween(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);

    // Compter vaccins par type
    @Query("SELECT v.vaccineType, COUNT(v) FROM Vaccine v GROUP BY v.vaccineType")
    List<Object[]> countVaccinesByType();

    // Vaccins les plus administrés
    @Query("SELECT v, COUNT(vr) as recordCount FROM Vaccine v " +
           "LEFT JOIN VaccinationRecord vr ON vr.vaccine = v " +
           "GROUP BY v ORDER BY recordCount DESC")
    List<Object[]> findMostAdministeredVaccines(Pageable pageable);
}

@Repository
interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {

    // Dossiers de vaccination par patient
    List<VaccinationRecord> findByPatientOrderByVaccinationDateDesc(Patient patient);

    // Vaccinations par vaccin
    List<VaccinationRecord> findByVaccine(Vaccine vaccine);

    // Vaccinations d'aujourd'hui
    @Query("SELECT vr FROM VaccinationRecord vr WHERE DATE(vr.vaccinationDate) = CURRENT_DATE")
    List<VaccinationRecord> findTodayVaccinations();

    // Vaccinations par période
    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.vaccinationDate BETWEEN :startDate AND :endDate")
    List<VaccinationRecord> findVaccinationsBetweenDates(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);

    // Rappels dus
    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.nextDueDate <= :date AND vr.isCompleted = false")
    List<VaccinationRecord> findDueVaccinations(@Param("date") LocalDate date);

    // Vaccinations complètes
    List<VaccinationRecord> findByIsCompleted(Boolean isCompleted);

    // Statistiques de vaccination
    @Query("SELECT v.vaccineName, COUNT(vr) FROM VaccinationRecord vr JOIN vr.vaccine v GROUP BY v.vaccineName")
    List<Object[]> getVaccinationStatistics();

    // Recherche par numéro de lot
    List<VaccinationRecord> findByBatchNumber(String batchNumber);

    // Patients avec vaccination complète pour un vaccin
    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.vaccine = :vaccine AND vr.isCompleted = true")
    List<VaccinationRecord> findCompletedVaccinationsByVaccine(@Param("vaccine") Vaccine vaccine);
}