package com.hospital.service;

import com.hospital.entity.Patient;
import com.hospital.entity.HospitalConfiguration;
import com.hospital.entity.MedicalCertificate;
import com.hospital.entity.Prescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintService {

    private final HospitalConfigurationService configurationService;

    /**
     * Génère une carte de patient pour impression
     */
    public String generatePatientCard(Patient patient) {
        try {
            HospitalConfiguration config = configurationService.getActiveConfiguration().orElse(new HospitalConfiguration());
            
            StringBuilder cardHtml = new StringBuilder();
            
            cardHtml.append("<!DOCTYPE html>\n")
                   .append("<html>\n")
                   .append("<head>\n")
                   .append("    <meta charset='UTF-8'>\n")
                   .append("    <title>Carte Patient - ").append(patient.getFirstName()).append(" ").append(patient.getLastName()).append("</title>\n")
                   .append("    <style>\n")
                   .append(getPatientCardStyles())
                   .append("    </style>\n")
                   .append("</head>\n")
                   .append("<body>\n")
                   .append("    <div class='patient-card'>\n");

            // En-tête avec logo
            cardHtml.append("        <div class='card-header'>\n");
            if (config.getLogoBase64() != null) {
                cardHtml.append("            <img src='").append(config.getLogoBase64()).append("' class='logo' alt='Logo'>\n");
            }
            cardHtml.append("            <div class='hospital-info'>\n")
                   .append("                <h2>").append(config.getHospitalName() != null ? config.getHospitalName() : "Hôpital Système").append("</h2>\n");
            if (config.getHospitalAddress() != null) {
                cardHtml.append("                <p>").append(config.getHospitalAddress()).append("</p>\n");
            }
            if (config.getPhone() != null) {
                cardHtml.append("                <p>Tél: ").append(config.getPhone()).append("</p>\n");
            }
            cardHtml.append("            </div>\n")
                   .append("        </div>\n");

            // Informations patient
            cardHtml.append("        <div class='card-content'>\n")
                   .append("            <div class='patient-info'>\n")
                   .append("                <div class='patient-details'>\n")
                   .append("                    <h3>CARTE PATIENT</h3>\n")
                   .append("                    <div class='info-row'>\n")
                   .append("                        <span class='label'>N° Patient:</span>\n")
                   .append("                        <span class='value'>").append(patient.getPatientNumber()).append("</span>\n")
                   .append("                    </div>\n")
                   .append("                    <div class='info-row'>\n")
                   .append("                        <span class='label'>Nom:</span>\n")
                   .append("                        <span class='value'>").append(patient.getLastName().toUpperCase()).append("</span>\n")
                   .append("                    </div>\n")
                   .append("                    <div class='info-row'>\n")
                   .append("                        <span class='label'>Prénom:</span>\n")
                   .append("                        <span class='value'>").append(patient.getFirstName()).append("</span>\n")
                   .append("                    </div>\n")
                   .append("                    <div class='info-row'>\n")
                   .append("                        <span class='label'>Sexe:</span>\n")
                   .append("                        <span class='value'>").append(patient.getGender().getDisplayName()).append("</span>\n")
                   .append("                    </div>\n");
            
            if (patient.getProfession() != null) {
                cardHtml.append("                    <div class='info-row'>\n")
                       .append("                        <span class='label'>Profession:</span>\n")
                       .append("                        <span class='value'>").append(patient.getProfession()).append("</span>\n")
                       .append("                    </div>\n");
            }
            
            if (patient.getPhone() != null) {
                cardHtml.append("                    <div class='info-row'>\n")
                       .append("                        <span class='label'>Téléphone:</span>\n")
                       .append("                        <span class='value'>").append(patient.getPhone()).append("</span>\n")
                       .append("                    </div>\n");
            }
            
            cardHtml.append("                </div>\n");

            // Photo du patient
            cardHtml.append("                <div class='patient-photo'>\n");
            if (patient.getPhotoBase64() != null) {
                cardHtml.append("                    <img src='").append(patient.getPhotoBase64()).append("' alt='Photo Patient'>\n");
            } else {
                cardHtml.append("                    <div class='no-photo'>\n")
                       .append("                        <span>Pas de photo</span>\n")
                       .append("                    </div>\n");
            }
            cardHtml.append("                </div>\n")
                   .append("            </div>\n");

            // Pied de carte
            cardHtml.append("            <div class='card-footer'>\n")
                   .append("                <p>Date d'émission: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n");
            if (config.getFooterText() != null && !config.getFooterText().isEmpty()) {
                cardHtml.append("                <p>").append(config.getFooterText()).append("</p>\n");
            }
            cardHtml.append("            </div>\n")
                   .append("        </div>\n")
                   .append("    </div>\n")
                   .append("</body>\n")
                   .append("</html>");

            return cardHtml.toString();
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération de la carte patient: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération de la carte patient: " + e.getMessage());
        }
    }

    /**
     * Génère une prescription pour impression
     */
    public String generatePrescription(Prescription prescription) {
        try {
            HospitalConfiguration config = configurationService.getActiveConfiguration().orElse(new HospitalConfiguration());
            
            StringBuilder prescriptionHtml = new StringBuilder();
            
            prescriptionHtml.append("<!DOCTYPE html>\n")
                          .append("<html>\n")
                          .append("<head>\n")
                          .append("    <meta charset='UTF-8'>\n")
                          .append("    <title>Ordonnance - ").append(prescription.getPatient().getFirstName()).append(" ").append(prescription.getPatient().getLastName()).append("</title>\n")
                          .append("    <style>\n")
                          .append(getPrescriptionStyles())
                          .append("    </style>\n")
                          .append("</head>\n")
                          .append("<body>\n")
                          .append("    <div class='prescription'>\n");

            // En-tête
            prescriptionHtml.append(generateDocumentHeader(config, "ORDONNANCE"));

            // Informations patient
            prescriptionHtml.append("        <div class='patient-section'>\n")
                          .append("            <h3>Patient</h3>\n")
                          .append("            <p><strong>").append(prescription.getPatient().getFirstName()).append(" ").append(prescription.getPatient().getLastName()).append("</strong></p>\n")
                          .append("            <p>N° Patient: ").append(prescription.getPatient().getPatientNumber()).append("</p>\n");
            if (prescription.getPatient().getDateOfBirth() != null) {
                prescriptionHtml.append("            <p>Né(e) le: ").append(prescription.getPatient().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n");
            }
            if (prescription.getPatient().getAddress() != null) {
                prescriptionHtml.append("            <p>").append(prescription.getPatient().getAddress()).append("</p>\n");
            }
            prescriptionHtml.append("        </div>\n");

            // Informations médecin
            prescriptionHtml.append("        <div class='doctor-section'>\n")
                          .append("            <h3>Médecin prescripteur</h3>\n")
                          .append("            <p><strong>Dr. ").append(prescription.getDoctor().getFirstName()).append(" ").append(prescription.getDoctor().getLastName()).append("</strong></p>\n")
                          .append("            <p>").append(prescription.getDoctor().getSpecialization()).append("</p>\n")
                          .append("            <p>N° Médecin: ").append(prescription.getDoctor().getDoctorNumber()).append("</p>\n")
                          .append("        </div>\n");

            // Médicaments
            prescriptionHtml.append("        <div class='medications-section'>\n")
                          .append("            <h3>Médicaments prescrits</h3>\n")
                          .append("            <div class='medications-content'>\n")
                          .append("                ").append(prescription.getMedications().replace("\n", "<br>")).append("\n")
                          .append("            </div>\n")
                          .append("        </div>\n");

            // Instructions
            if (prescription.getInstructions() != null && !prescription.getInstructions().isEmpty()) {
                prescriptionHtml.append("        <div class='instructions-section'>\n")
                              .append("            <h3>Instructions</h3>\n")
                              .append("            <p>").append(prescription.getInstructions().replace("\n", "<br>")).append("</p>\n")
                              .append("        </div>\n");
            }

            // Pied de document
            prescriptionHtml.append(generateDocumentFooter(config, prescription.getCreatedAt()));
            
            prescriptionHtml.append("    </div>\n")
                          .append("</body>\n")
                          .append("</html>");

            return prescriptionHtml.toString();
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération de l'ordonnance: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération de l'ordonnance: " + e.getMessage());
        }
    }

    /**
     * Génère un certificat médical pour impression
     */
    public String generateMedicalCertificate(MedicalCertificate certificate) {
        try {
            HospitalConfiguration config = configurationService.getActiveConfiguration().orElse(new HospitalConfiguration());
            
            StringBuilder certificateHtml = new StringBuilder();
            
            certificateHtml.append("<!DOCTYPE html>\n")
                          .append("<html>\n")
                          .append("<head>\n")
                          .append("    <meta charset='UTF-8'>\n")
                          .append("    <title>Certificat Médical - ").append(certificate.getPatient().getFirstName()).append(" ").append(certificate.getPatient().getLastName()).append("</title>\n")
                          .append("    <style>\n")
                          .append(getCertificateStyles())
                          .append("    </style>\n")
                          .append("</head>\n")
                          .append("<body>\n")
                          .append("    <div class='certificate'>\n");

            // En-tête
            certificateHtml.append(generateDocumentHeader(config, "CERTIFICAT MÉDICAL"));

            // Corps du certificat
            certificateHtml.append("        <div class='certificate-body'>\n")
                          .append("            <p class='declaration'>Je soussigné(e), <strong>Dr. ")
                          .append(certificate.getDoctor().getFirstName()).append(" ").append(certificate.getDoctor().getLastName())
                          .append("</strong>, ").append(certificate.getDoctor().getSpecialization())
                          .append(", certifie avoir examiné ce jour :</p>\n")
                          .append("            <div class='patient-info'>\n")
                          .append("                <p><strong>").append(certificate.getPatient().getFirstName()).append(" ").append(certificate.getPatient().getLastName()).append("</strong></p>\n")
                          .append("                <p>N° Patient: ").append(certificate.getPatient().getPatientNumber()).append("</p>\n");
            
            if (certificate.getPatient().getDateOfBirth() != null) {
                certificateHtml.append("                <p>Né(e) le: ").append(certificate.getPatient().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n");
            }
            
            certificateHtml.append("            </div>\n")
                          .append("            <div class='certificate-content'>\n")
                          .append("                <p>").append(certificate.getContent().replace("\n", "</p>\n                <p>")).append("</p>\n")
                          .append("            </div>\n");

            if (certificate.getRestDays() != null && certificate.getRestDays() > 0) {
                certificateHtml.append("            <div class='rest-days'>\n")
                              .append("                <p><strong>Arrêt de travail prescrit : ").append(certificate.getRestDays()).append(" jour(s)</strong></p>\n")
                              .append("            </div>\n");
            }

            certificateHtml.append("        </div>\n");

            // Signature
            certificateHtml.append("        <div class='signature-section'>\n")
                          .append("            <div class='signature-left'>\n")
                          .append("                <p>Fait le : ").append(certificate.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n")
                          .append("            </div>\n")
                          .append("            <div class='signature-right'>\n")
                          .append("                <p>Signature du médecin</p>\n");
            
            if (config.getSignatureBase64() != null) {
                certificateHtml.append("                <img src='").append(config.getSignatureBase64()).append("' class='signature-img' alt='Signature'>\n");
            } else {
                certificateHtml.append("                <div class='signature-space'></div>\n");
            }
            
            certificateHtml.append("                <p><strong>Dr. ").append(certificate.getDoctor().getFirstName()).append(" ").append(certificate.getDoctor().getLastName()).append("</strong></p>\n")
                          .append("            </div>\n")
                          .append("        </div>\n");

            // Pied de document
            certificateHtml.append(generateDocumentFooter(config, certificate.getCreatedAt()));
            
            certificateHtml.append("    </div>\n")
                          .append("</body>\n")
                          .append("</html>");

            return certificateHtml.toString();
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du certificat médical: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du certificat médical: " + e.getMessage());
        }
    }

    /**
     * Génère l'en-tête commun des documents
     */
    private String generateDocumentHeader(HospitalConfiguration config, String documentType) {
        StringBuilder header = new StringBuilder();
        
        header.append("        <div class='document-header'>\n");
        
        if (config.getLogoBase64() != null) {
            header.append("            <img src='").append(config.getLogoBase64()).append("' class='logo' alt='Logo'>\n");
        }
        
        header.append("            <div class='hospital-info'>\n")
              .append("                <h1>").append(config.getHospitalName() != null ? config.getHospitalName() : "Hôpital Système").append("</h1>\n");
        
        if (config.getHospitalAddress() != null) {
            header.append("                <p>").append(config.getHospitalAddress()).append("</p>\n");
        }
        
        if (config.getCity() != null) {
            header.append("                <p>").append(config.getCity());
            if (config.getPostalCode() != null) {
                header.append(" ").append(config.getPostalCode());
            }
            header.append("</p>\n");
        }
        
        if (config.getPhone() != null) {
            header.append("                <p>Tél: ").append(config.getPhone());
            if (config.getFax() != null) {
                header.append(" - Fax: ").append(config.getFax());
            }
            header.append("</p>\n");
        }
        
        if (config.getEmail() != null) {
            header.append("                <p>Email: ").append(config.getEmail()).append("</p>\n");
        }
        
        header.append("            </div>\n")
              .append("            <h2 class='document-title'>").append(documentType).append("</h2>\n")
              .append("        </div>\n");
        
        if (config.getHeaderText() != null && !config.getHeaderText().isEmpty()) {
            header.append("        <div class='header-text'>\n")
                  .append("            <p>").append(config.getHeaderText()).append("</p>\n")
                  .append("        </div>\n");
        }
        
        return header.toString();
    }

    /**
     * Génère le pied de page commun des documents
     */
    private String generateDocumentFooter(HospitalConfiguration config, LocalDateTime createdAt) {
        StringBuilder footer = new StringBuilder();
        
        footer.append("        <div class='document-footer'>\n")
              .append("            <div class='footer-info'>\n")
              .append("                <p>Document généré le ").append(createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))).append("</p>\n");
        
        if (config.getFooterText() != null && !config.getFooterText().isEmpty()) {
            footer.append("                <p>").append(config.getFooterText()).append("</p>\n");
        }
        
        if (config.getLicenseNumber() != null) {
            footer.append("                <p>Licence n° ").append(config.getLicenseNumber()).append("</p>\n");
        }
        
        footer.append("            </div>\n")
              .append("        </div>\n");
        
        return footer.toString();
    }

    /**
     * Styles CSS pour la carte patient
     */
    private String getPatientCardStyles() {
        return """
            body { margin: 0; padding: 20px; font-family: Arial, sans-serif; }
            .patient-card { 
                width: 85.6mm; 
                height: 53.98mm; 
                border: 2px solid #333; 
                border-radius: 8px; 
                padding: 5mm; 
                box-sizing: border-box; 
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            }
            .card-header { 
                display: flex; 
                align-items: center; 
                margin-bottom: 3mm; 
                border-bottom: 1px solid #ddd; 
                padding-bottom: 2mm; 
            }
            .logo { 
                width: 8mm; 
                height: 8mm; 
                margin-right: 2mm; 
                object-fit: contain; 
            }
            .hospital-info h2 { 
                margin: 0; 
                font-size: 8pt; 
                color: #2c5aa0; 
                font-weight: bold; 
            }
            .hospital-info p { 
                margin: 0; 
                font-size: 6pt; 
                color: #666; 
            }
            .card-content { 
                height: calc(100% - 15mm); 
            }
            .patient-info { 
                display: flex; 
                justify-content: space-between; 
                height: 100%; 
            }
            .patient-details { 
                flex: 1; 
                margin-right: 2mm; 
            }
            .patient-details h3 { 
                margin: 0 0 2mm 0; 
                font-size: 7pt; 
                color: #2c5aa0; 
                text-align: center; 
                font-weight: bold; 
                text-decoration: underline; 
            }
            .info-row { 
                display: flex; 
                margin-bottom: 1mm; 
                font-size: 6pt; 
            }
            .label { 
                font-weight: bold; 
                width: 12mm; 
                color: #333; 
            }
            .value { 
                color: #000; 
                font-weight: normal; 
            }
            .patient-photo { 
                width: 12mm; 
                height: 15mm; 
                border: 1px solid #ddd; 
                display: flex; 
                align-items: center; 
                justify-content: center; 
                border-radius: 3px; 
                overflow: hidden; 
            }
            .patient-photo img { 
                width: 100%; 
                height: 100%; 
                object-fit: cover; 
            }
            .no-photo { 
                font-size: 5pt; 
                color: #999; 
                text-align: center; 
            }
            .card-footer { 
                position: absolute; 
                bottom: 2mm; 
                left: 2mm; 
                right: 2mm; 
                font-size: 5pt; 
                color: #666; 
                text-align: center; 
                border-top: 1px solid #eee; 
                padding-top: 1mm; 
            }
            .card-footer p { 
                margin: 0; 
            }
            @media print {
                body { margin: 0; padding: 0; }
                .patient-card { margin: 0; }
            }
        """;
    }

    /**
     * Styles CSS pour les prescriptions
     */
    private String getPrescriptionStyles() {
        return """
            body { margin: 0; padding: 20px; font-family: Arial, sans-serif; background: #fff; }
            .prescription { max-width: 210mm; margin: 0 auto; padding: 20px; background: #fff; }
            .document-header { 
                display: flex; 
                align-items: flex-start; 
                margin-bottom: 30px; 
                border-bottom: 2px solid #2c5aa0; 
                padding-bottom: 15px; 
            }
            .logo { width: 60px; height: 60px; margin-right: 20px; object-fit: contain; }
            .hospital-info { flex: 1; }
            .hospital-info h1 { margin: 0; color: #2c5aa0; font-size: 22px; }
            .hospital-info p { margin: 2px 0; color: #666; font-size: 12px; }
            .document-title { 
                margin: 0; 
                color: #2c5aa0; 
                font-size: 18px; 
                text-align: center; 
                font-weight: bold; 
                text-decoration: underline; 
            }
            .header-text { margin: 15px 0; padding: 10px; background: #f8f9fa; border-left: 4px solid #2c5aa0; }
            .patient-section, .doctor-section { 
                margin: 20px 0; 
                padding: 15px; 
                border: 1px solid #ddd; 
                border-radius: 5px; 
                background: #f9f9f9; 
            }
            .patient-section h3, .doctor-section h3 { 
                margin: 0 0 10px 0; 
                color: #2c5aa0; 
                font-size: 14px; 
            }
            .medications-section { 
                margin: 30px 0; 
                padding: 20px; 
                border: 2px solid #28a745; 
                border-radius: 5px; 
                background: #f8fff9; 
            }
            .medications-section h3 { 
                margin: 0 0 15px 0; 
                color: #28a745; 
                font-size: 16px; 
            }
            .medications-content { 
                font-size: 14px; 
                line-height: 1.6; 
                font-weight: bold; 
            }
            .instructions-section { 
                margin: 20px 0; 
                padding: 15px; 
                background: #fff3cd; 
                border: 1px solid #ffeaa7; 
                border-radius: 5px; 
            }
            .instructions-section h3 { 
                margin: 0 0 10px 0; 
                color: #856404; 
                font-size: 14px; 
            }
            .document-footer { 
                margin-top: 50px; 
                padding-top: 20px; 
                border-top: 1px solid #ddd; 
                text-align: center; 
                font-size: 12px; 
                color: #666; 
            }
            @media print {
                body { margin: 0; padding: 0; }
                .prescription { margin: 0; padding: 15px; }
            }
        """;
    }

    /**
     * Styles CSS pour les certificats médicaux
     */
    private String getCertificateStyles() {
        return """
            body { margin: 0; padding: 20px; font-family: Arial, sans-serif; background: #fff; }
            .certificate { max-width: 210mm; margin: 0 auto; padding: 30px; background: #fff; }
            .document-header { 
                display: flex; 
                align-items: flex-start; 
                margin-bottom: 40px; 
                border-bottom: 3px solid #dc3545; 
                padding-bottom: 20px; 
            }
            .logo { width: 80px; height: 80px; margin-right: 25px; object-fit: contain; }
            .hospital-info { flex: 1; }
            .hospital-info h1 { margin: 0; color: #dc3545; font-size: 24px; }
            .hospital-info p { margin: 3px 0; color: #666; font-size: 14px; }
            .document-title { 
                margin: 0; 
                color: #dc3545; 
                font-size: 20px; 
                text-align: center; 
                font-weight: bold; 
                text-decoration: underline; 
                letter-spacing: 2px; 
            }
            .header-text { margin: 20px 0; padding: 15px; background: #f8f9fa; border-left: 5px solid #dc3545; }
            .certificate-body { margin: 30px 0; font-size: 16px; line-height: 1.8; }
            .declaration { font-style: italic; margin-bottom: 20px; }
            .patient-info { 
                margin: 20px 0; 
                padding: 15px; 
                background: #f8f9fa; 
                border-radius: 5px; 
                border-left: 5px solid #dc3545; 
            }
            .certificate-content { 
                margin: 30px 0; 
                padding: 20px; 
                border: 1px solid #ddd; 
                border-radius: 5px; 
                background: #fff; 
                min-height: 100px; 
            }
            .rest-days { 
                margin: 20px 0; 
                padding: 15px; 
                background: #fff3cd; 
                border: 2px solid #ffc107; 
                border-radius: 5px; 
                text-align: center; 
            }
            .signature-section { 
                display: flex; 
                justify-content: space-between; 
                margin-top: 50px; 
                align-items: flex-end; 
            }
            .signature-left, .signature-right { flex: 1; }
            .signature-right { text-align: right; }
            .signature-img { width: 100px; height: 50px; object-fit: contain; margin: 10px 0; }
            .signature-space { 
                width: 150px; 
                height: 50px; 
                border-bottom: 1px solid #000; 
                margin: 10px 0 10px auto; 
            }
            .document-footer { 
                margin-top: 50px; 
                padding-top: 20px; 
                border-top: 2px solid #ddd; 
                text-align: center; 
                font-size: 12px; 
                color: #666; 
            }
            @media print {
                body { margin: 0; padding: 0; }
                .certificate { margin: 0; padding: 20px; }
            }
        """;
    }
}