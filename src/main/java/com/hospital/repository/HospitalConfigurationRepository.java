package com.hospital.repository;

import com.hospital.entity.HospitalConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalConfigurationRepository extends JpaRepository<HospitalConfiguration, Long> {

    @Query("SELECT h FROM HospitalConfiguration h ORDER BY h.createdAt DESC")
    Optional<HospitalConfiguration> findActiveConfiguration();

    @Query("SELECT h.hospitalName FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findHospitalName();

    @Query("SELECT h.logoBase64 FROM HospitalConfiguration h WHERE h.logoBase64 IS NOT NULL ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findLogo();

    @Query("SELECT h.patientNumberPrefix FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findPatientNumberPrefix();

    @Query("SELECT h.doctorNumberPrefix FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findDoctorNumberPrefix();

    @Query("SELECT h.billNumberPrefix FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findBillNumberPrefix();

    @Query("SELECT h.taxRate FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<Double> findTaxRate();

    @Query("SELECT h.currency FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findCurrency();

    @Query("SELECT h.language FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findLanguage();

    @Query("SELECT h.timezone FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findTimezone();

    @Query("SELECT h.footerText FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findFooterText();

    @Query("SELECT h.headerText FROM HospitalConfiguration h ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findHeaderText();

    @Query("SELECT h.signatureBase64 FROM HospitalConfiguration h WHERE h.signatureBase64 IS NOT NULL ORDER BY h.createdAt DESC LIMIT 1")
    Optional<String> findSignature();
}