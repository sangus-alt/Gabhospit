package com.hospital.repository;

import com.hospital.entity.LabResult;
import com.hospital.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {

    @Query("SELECT lr FROM LabResult lr WHERE lr.patient = :patient ORDER BY lr.createdDate DESC")
    List<LabResult> findByPatient(@Param("patient") Patient patient);

    @Query("SELECT lr FROM LabResult lr WHERE lr.patient.id = :patientId ORDER BY lr.createdDate DESC")
    List<LabResult> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.doctor.id = :doctorId ORDER BY lr.createdDate DESC")
    List<LabResult> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.status = :status ORDER BY lr.createdDate DESC")
    List<LabResult> findByStatus(@Param("status") String status);

    @Query("SELECT lr FROM LabResult lr WHERE lr.createdDate >= :startDate AND lr.createdDate <= :endDate ORDER BY lr.createdDate DESC")
    List<LabResult> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT lr FROM LabResult lr WHERE lr.isUrgent = true ORDER BY lr.createdDate DESC")
    List<LabResult> findUrgentResults();

    @Query("SELECT lr FROM LabResult lr WHERE lr.isValidated = false ORDER BY lr.createdDate DESC")
    List<LabResult> findPendingValidation();

    @Query("SELECT lr FROM LabResult lr WHERE lr.isReleased = false AND lr.isValidated = true ORDER BY lr.createdDate DESC")
    List<LabResult> findPendingRelease();

    @Query("SELECT COUNT(lr) FROM LabResult lr WHERE lr.createdDate >= :startDate AND lr.createdDate <= :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(lr) FROM LabResult lr WHERE lr.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT COUNT(lr) FROM LabResult lr WHERE lr.isUrgent = true")
    Long countUrgentResults();

    @Query("SELECT lr FROM LabResult lr WHERE " +
           "(:patientName IS NULL OR CONCAT(lr.patient.firstName, ' ', lr.patient.lastName) LIKE %:patientName%) AND " +
           "(:doctorName IS NULL OR CONCAT(lr.doctor.firstName, ' ', lr.doctor.lastName) LIKE %:doctorName%) AND " +
           "(:status IS NULL OR lr.status = :status)")
    Page<LabResult> searchResults(@Param("patientName") String patientName, 
                                  @Param("doctorName") String doctorName, 
                                  @Param("status") String status, 
                                  Pageable pageable);

    @Query("SELECT lr FROM LabResult lr WHERE lr.createdDate >= :startDate AND lr.createdDate <= :endDate")
    Page<LabResult> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate, 
                                   Pageable pageable);
}