package com.hospital.service;

import com.hospital.entity.HospitalConfiguration;
import com.hospital.repository.HospitalConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HospitalConfigurationService {

    private final HospitalConfigurationRepository configurationRepository;

    /**
     * Récupère la configuration active de l'hôpital
     */
    public Optional<HospitalConfiguration> getActiveConfiguration() {
        return configurationRepository.findActiveConfiguration();
    }

    /**
     * Crée ou met à jour la configuration de l'hôpital
     */
    public HospitalConfiguration saveConfiguration(HospitalConfiguration configuration) {
        try {
            log.info("Sauvegarde de la configuration de l'hôpital: {}", configuration.getHospitalName());
            
            // Validation des données
            validateConfiguration(configuration);
            
            HospitalConfiguration savedConfig = configurationRepository.save(configuration);
            log.info("Configuration sauvegardée avec succès avec l'ID: {}", savedConfig.getId());
            
            return savedConfig;
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde de la configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde de la configuration: " + e.getMessage());
        }
    }

    /**
     * Upload et sauvegarde le logo de l'hôpital
     */
    public void uploadLogo(MultipartFile logoFile) {
        try {
            if (logoFile == null || logoFile.isEmpty()) {
                throw new IllegalArgumentException("Le fichier logo ne peut pas être vide");
            }

            // Validation du type de fichier
            String contentType = logoFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Le fichier doit être une image");
            }

            // Validation de la taille (max 5MB)
            if (logoFile.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("La taille du fichier ne peut pas dépasser 5MB");
            }

            // Conversion en Base64
            String logoBase64 = Base64.getEncoder().encodeToString(logoFile.getBytes());
            
            // Récupérer ou créer la configuration
            HospitalConfiguration config = getActiveConfiguration().orElse(new HospitalConfiguration());
            config.setLogoBase64("data:" + contentType + ";base64," + logoBase64);
            
            configurationRepository.save(config);
            log.info("Logo uploadé avec succès");
            
        } catch (IOException e) {
            log.error("Erreur lors de l'upload du logo: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload du logo: " + e.getMessage());
        }
    }

    /**
     * Upload et sauvegarde la signature
     */
    public void uploadSignature(MultipartFile signatureFile) {
        try {
            if (signatureFile == null || signatureFile.isEmpty()) {
                throw new IllegalArgumentException("Le fichier signature ne peut pas être vide");
            }

            // Validation du type de fichier
            String contentType = signatureFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Le fichier doit être une image");
            }

            // Conversion en Base64
            String signatureBase64 = Base64.getEncoder().encodeToString(signatureFile.getBytes());
            
            // Récupérer ou créer la configuration
            HospitalConfiguration config = getActiveConfiguration().orElse(new HospitalConfiguration());
            config.setSignatureBase64("data:" + contentType + ";base64," + signatureBase64);
            
            configurationRepository.save(config);
            log.info("Signature uploadée avec succès");
            
        } catch (IOException e) {
            log.error("Erreur lors de l'upload de la signature: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload de la signature: " + e.getMessage());
        }
    }

    /**
     * Récupère le nom de l'hôpital
     */
    public String getHospitalName() {
        return configurationRepository.findHospitalName().orElse("Hôpital Système");
    }

    /**
     * Récupère le logo de l'hôpital
     */
    public String getHospitalLogo() {
        return configurationRepository.findLogo().orElse(null);
    }

    /**
     * Récupère le préfixe des numéros patient
     */
    public String getPatientNumberPrefix() {
        return configurationRepository.findPatientNumberPrefix().orElse("P");
    }

    /**
     * Récupère le préfixe des numéros médecin
     */
    public String getDoctorNumberPrefix() {
        return configurationRepository.findDoctorNumberPrefix().orElse("D");
    }

    /**
     * Récupère le préfixe des numéros de facture
     */
    public String getBillNumberPrefix() {
        return configurationRepository.findBillNumberPrefix().orElse("BILL");
    }

    /**
     * Récupère le taux de taxe
     */
    public Double getTaxRate() {
        return configurationRepository.findTaxRate().orElse(0.20); // 20% par défaut
    }

    /**
     * Récupère la devise
     */
    public String getCurrency() {
        return configurationRepository.findCurrency().orElse("EUR");
    }

    /**
     * Récupère la langue
     */
    public String getLanguage() {
        return configurationRepository.findLanguage().orElse("FR");
    }

    /**
     * Récupère le fuseau horaire
     */
    public String getTimezone() {
        return configurationRepository.findTimezone().orElse("Europe/Paris");
    }

    /**
     * Récupère le texte du pied de page
     */
    public String getFooterText() {
        return configurationRepository.findFooterText().orElse("");
    }

    /**
     * Récupère le texte de l'en-tête
     */
    public String getHeaderText() {
        return configurationRepository.findHeaderText().orElse("");
    }

    /**
     * Récupère la signature
     */
    public String getSignature() {
        return configurationRepository.findSignature().orElse(null);
    }

    /**
     * Effectue une sauvegarde de la configuration
     */
    public void performBackup() {
        try {
            HospitalConfiguration config = getActiveConfiguration().orElse(new HospitalConfiguration());
            config.setLastBackupDate(LocalDateTime.now());
            configurationRepository.save(config);
            log.info("Sauvegarde effectuée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    /**
     * Active ou désactive le mode maintenance
     */
    public void setMaintenanceMode(boolean enabled, String message) {
        try {
            HospitalConfiguration config = getActiveConfiguration().orElse(new HospitalConfiguration());
            config.setMaintenanceMode(enabled);
            config.setMaintenanceMessage(message);
            configurationRepository.save(config);
            log.info("Mode maintenance {}: {}", enabled ? "activé" : "désactivé", message);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du mode maintenance: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du mode maintenance: " + e.getMessage());
        }
    }

    /**
     * Valide la configuration
     */
    private void validateConfiguration(HospitalConfiguration configuration) {
        if (configuration.getHospitalName() == null || configuration.getHospitalName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'hôpital est obligatoire");
        }

        if (configuration.getTaxRate() != null && (configuration.getTaxRate() < 0 || configuration.getTaxRate() > 1)) {
            throw new IllegalArgumentException("Le taux de taxe doit être entre 0 et 1");
        }

        if (configuration.getSessionTimeoutMinutes() != null && configuration.getSessionTimeoutMinutes() < 5) {
            throw new IllegalArgumentException("Le timeout de session doit être d'au moins 5 minutes");
        }

        if (configuration.getMaxLoginAttempts() != null && configuration.getMaxLoginAttempts() < 1) {
            throw new IllegalArgumentException("Le nombre maximum de tentatives de connexion doit être d'au moins 1");
        }
    }

    /**
     * Réinitialise la configuration aux valeurs par défaut
     */
    public HospitalConfiguration resetToDefault() {
        try {
            HospitalConfiguration defaultConfig = new HospitalConfiguration();
            defaultConfig.setHospitalName("Hôpital Système");
            defaultConfig.setHospitalType("PUBLIC");
            defaultConfig.setLanguage("FR");
            defaultConfig.setTimezone("Europe/Paris");
            defaultConfig.setCurrency("EUR");
            defaultConfig.setTaxRate(0.20);
            defaultConfig.setPatientNumberPrefix("P");
            defaultConfig.setDoctorNumberPrefix("D");
            defaultConfig.setBillNumberPrefix("BILL");
            defaultConfig.setSessionTimeoutMinutes(30);
            defaultConfig.setMaxLoginAttempts(5);
            defaultConfig.setAutoBackupEnabled(true);
            defaultConfig.setBackupFrequencyHours(24);
            defaultConfig.setMaintenanceMode(false);
            
            return configurationRepository.save(defaultConfig);
        } catch (Exception e) {
            log.error("Erreur lors de la réinitialisation de la configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la réinitialisation de la configuration: " + e.getMessage());
        }
    }
}