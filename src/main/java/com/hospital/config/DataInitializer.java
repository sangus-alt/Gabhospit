package com.hospital.config;

import com.hospital.entity.*;
import com.hospital.repository.*;
import com.hospital.service.HospitalConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PharmacyRepository pharmacyRepository;
    private final LabTestRepository labTestRepository;
    private final PasswordEncoder passwordEncoder;
    private final HospitalConfigurationService configurationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("🏥 Initialisation des données du système hospitalier...");
        
        initializePermissions();
        initializeRoles();
        initializeUsers();
        initializeDepartments();
        initializeDoctors();
        initializePatients();
        initializeMedications();
        initializeLabTests();
        initializeConfiguration();
        
        log.info("✅ Initialisation des données terminée avec succès !");
        log.info("🔑 Compte administrateur créé:");
        log.info("   👤 Utilisateur: admin");
        log.info("   🔒 Mot de passe: Admin123!");
        log.info("   🌐 Interface: http://localhost:8080/admin-dashboard.html");
    }

    private void initializePermissions() {
        if (permissionRepository.count() == 0) {
            log.info("Création des permissions...");
            
            List<String> permissions = Arrays.asList(
                "READ_PATIENTS", "WRITE_PATIENTS", "DELETE_PATIENTS",
                "READ_DOCTORS", "WRITE_DOCTORS", "DELETE_DOCTORS",
                "READ_CONSULTATIONS", "WRITE_CONSULTATIONS", "DELETE_CONSULTATIONS",
                "READ_APPOINTMENTS", "WRITE_APPOINTMENTS", "DELETE_APPOINTMENTS",
                "READ_LAB", "WRITE_LAB", "DELETE_LAB",
                "READ_PHARMACY", "WRITE_PHARMACY", "DELETE_PHARMACY",
                "READ_BILLING", "WRITE_BILLING", "DELETE_BILLING",
                "READ_QUEUE", "WRITE_QUEUE", "DELETE_QUEUE",
                "READ_REPORTS", "WRITE_REPORTS",
                "ADMIN_ACCESS", "USER_MANAGEMENT"
            );
            
            permissions.forEach(permName -> {
                Permission permission = new Permission();
                permission.setName(permName);
                permission.setDescription("Permission pour " + permName.toLowerCase().replace("_", " "));
                permissionRepository.save(permission);
            });
            
            log.info("✅ {} permissions créées", permissions.size());
        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            log.info("Création des rôles...");
            
            // Rôle Administrateur
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrateur système - Tous droits");
            adminRole.setPermissions(permissionRepository.findAll().stream().collect(java.util.stream.Collectors.toSet()));
            roleRepository.save(adminRole);
            
            // Rôle Médecin
            Role doctorRole = new Role();
            doctorRole.setName("DOCTOR");
            doctorRole.setDescription("Médecin - Accès patients, consultations, prescriptions");
            roleRepository.save(doctorRole);
            
            // Rôle Infirmier
            Role nurseRole = new Role();
            nurseRole.setName("NURSE");
            nurseRole.setDescription("Infirmier - Soins, médicaments, vaccins");
            roleRepository.save(nurseRole);
            
            // Rôle Pharmacien
            Role pharmacistRole = new Role();
            pharmacistRole.setName("PHARMACIST");
            pharmacistRole.setDescription("Pharmacien - Pharmacie, stocks médicaments");
            roleRepository.save(pharmacistRole);
            
            // Rôle Réceptionniste
            Role receptionistRole = new Role();
            receptionistRole.setName("RECEPTIONIST");
            receptionistRole.setDescription("Réceptionniste - Patients, rendez-vous, file d'attente");
            roleRepository.save(receptionistRole);
            
            log.info("✅ 5 rôles créés");
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Création des utilisateurs...");
            
            // Utilisateur administrateur
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@hospital.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setFirstName("Administrateur");
            admin.setLastName("Système");
            admin.setIsActive(true);
            admin.setCreatedDate(LocalDateTime.now());
            admin.setRole(roleRepository.findByName("ADMIN").orElse(null));
            userRepository.save(admin);
            
            // Utilisateur médecin
            User doctor = new User();
            doctor.setUsername("dr.martin");
            doctor.setEmail("dr.martin@hospital.com");
            doctor.setPassword(passwordEncoder.encode("Doctor123!"));
            doctor.setFirstName("Dr. Jean");
            doctor.setLastName("Martin");
            doctor.setIsActive(true);
            doctor.setCreatedDate(LocalDateTime.now());
            doctor.setRole(roleRepository.findByName("DOCTOR").orElse(null));
            userRepository.save(doctor);
            
            log.info("✅ Utilisateurs créés");
        }
    }

    private void initializeDepartments() {
        if (departmentRepository.count() == 0) {
            log.info("Création des départements...");
            
            List<String[]> departments = Arrays.asList(
                new String[]{"CARD", "Cardiologie", "Département de cardiologie"},
                new String[]{"NEUR", "Neurologie", "Département de neurologie"},
                new String[]{"ORTH", "Orthopédie", "Département d'orthopédie"},
                new String[]{"PEDI", "Pédiatrie", "Département de pédiatrie"},
                new String[]{"URG", "Urgences", "Service des urgences"},
                new String[]{"LAB", "Laboratoire", "Laboratoire d'analyses médicales"},
                new String[]{"RAD", "Radiologie", "Service de radiologie"},
                new String[]{"PHAR", "Pharmacie", "Pharmacie hospitalière"},
                new String[]{"GYNE", "Gynécologie", "Département de gynécologie"},
                new String[]{"CHIR", "Chirurgie", "Département de chirurgie générale"}
            );
            
            departments.forEach(dept -> {
                Department department = new Department();
                department.setCode(dept[0]);
                department.setName(dept[1]);
                department.setDescription(dept[2]);
                department.setIsActive(true);
                departmentRepository.save(department);
            });
            
            log.info("✅ {} départements créés", departments.size());
        }
    }

    private void initializeDoctors() {
        if (doctorRepository.count() == 0) {
            log.info("Création des médecins...");
            
            Department cardiology = departmentRepository.findByCode("CARD").orElse(null);
            Department neurology = departmentRepository.findByCode("NEUR").orElse(null);
            Department emergency = departmentRepository.findByCode("URG").orElse(null);
            
            // Dr. Martin - Cardiologue
            Doctor drMartin = new Doctor();
            drMartin.setDoctorNumber("D" + System.currentTimeMillis() + "001");
            drMartin.setFirstName("Jean");
            drMartin.setLastName("Martin");
            drMartin.setEmail("dr.martin@hospital.com");
            drMartin.setPhone("0123456789");
            drMartin.setSpecialization("Cardiologie");
            drMartin.setDepartment(cardiology);
            drMartin.setLicenseNumber("LIC123456");
            drMartin.setExperienceYears(15);
            drMartin.setConsultationFee(80.0);
            drMartin.setIsActive(true);
            drMartin.setIsAvailable(true);
            drMartin.setHireDate(LocalDateTime.now().minusYears(5));
            doctorRepository.save(drMartin);
            
            // Dr. Dubois - Neurologue
            Doctor drDubois = new Doctor();
            drDubois.setDoctorNumber("D" + System.currentTimeMillis() + "002");
            drDubois.setFirstName("Marie");
            drDubois.setLastName("Dubois");
            drDubois.setEmail("dr.dubois@hospital.com");
            drDubois.setPhone("0123456790");
            drDubois.setSpecialization("Neurologie");
            drDubois.setDepartment(neurology);
            drDubois.setLicenseNumber("LIC123457");
            drDubois.setExperienceYears(12);
            drDubois.setConsultationFee(90.0);
            drDubois.setIsActive(true);
            drDubois.setIsAvailable(true);
            drDubois.setHireDate(LocalDateTime.now().minusYears(3));
            doctorRepository.save(drDubois);
            
            // Dr. Urgentiste
            Doctor drUrgence = new Doctor();
            drUrgence.setDoctorNumber("D" + System.currentTimeMillis() + "003");
            drUrgence.setFirstName("Pierre");
            drUrgence.setLastName("Urgence");
            drUrgence.setEmail("dr.urgence@hospital.com");
            drUrgence.setPhone("0123456791");
            drUrgence.setSpecialization("Médecine d'urgence");
            drUrgence.setDepartment(emergency);
            drUrgence.setLicenseNumber("LIC123458");
            drUrgence.setExperienceYears(8);
            drUrgence.setConsultationFee(70.0);
            drUrgence.setIsActive(true);
            drUrgence.setIsAvailable(true);
            drUrgence.setHireDate(LocalDateTime.now().minusYears(2));
            doctorRepository.save(drUrgence);
            
            log.info("✅ 3 médecins créés");
        }
    }

    private void initializePatients() {
        if (patientRepository.count() == 0) {
            log.info("Création des patients de démonstration...");
            
            // Patient 1
            Patient patient1 = new Patient();
            patient1.setPatientNumber("P" + System.currentTimeMillis() + "001");
            patient1.setFirstName("Jean");
            patient1.setLastName("Dupont");
            patient1.setDateOfBirth(LocalDate.of(1980, 5, 15));
            patient1.setGender(Patient.Gender.MALE);
            patient1.setPhone("0123456789");
            patient1.setEmail("jean.dupont@email.com");
            patient1.setAddress("123 Rue de la Paix");
            patient1.setCity("Paris");
            patient1.setPostalCode("75001");
            patient1.setRegistrationDate(LocalDateTime.now().minusDays(30));
            patientRepository.save(patient1);
            
            // Patient 2
            Patient patient2 = new Patient();
            patient2.setPatientNumber("P" + System.currentTimeMillis() + "002");
            patient2.setFirstName("Marie");
            patient2.setLastName("Durand");
            patient2.setDateOfBirth(LocalDate.of(1992, 8, 22));
            patient2.setGender(Patient.Gender.FEMALE);
            patient2.setPhone("0123456790");
            patient2.setEmail("marie.durand@email.com");
            patient2.setAddress("456 Avenue des Champs");
            patient2.setCity("Lyon");
            patient2.setPostalCode("69001");
            patient2.setRegistrationDate(LocalDateTime.now().minusDays(15));
            patientRepository.save(patient2);
            
            log.info("✅ 2 patients de démonstration créés");
        }
    }

    private void initializeMedications() {
        if (pharmacyRepository.count() == 0) {
            log.info("Création des médicaments...");
            
            // Médicament 1
            Medication med1 = new Medication();
            med1.setMedicationCode("MED" + System.currentTimeMillis() + "001");
            med1.setBrandName("Doliprane");
            med1.setGenericName("Paracétamol");
            med1.setManufacturer("Sanofi");
            med1.setCategory("Antalgique");
            med1.setDosage("1000mg");
            med1.setPrice(5.50);
            med1.setCurrentStock(100);
            med1.setMinimumStock(20);
            med1.setExpiryDate(LocalDate.now().plusYears(2));
            med1.setRequiresPrescription(false);
            med1.setIsActive(true);
            med1.setCreatedDate(LocalDateTime.now());
            pharmacyRepository.save(med1);
            
            // Médicament 2
            Medication med2 = new Medication();
            med2.setMedicationCode("MED" + System.currentTimeMillis() + "002");
            med2.setBrandName("Amoxicilline");
            med2.setGenericName("Amoxicilline");
            med2.setManufacturer("Biogaran");
            med2.setCategory("Antibiotique");
            med2.setDosage("500mg");
            med2.setPrice(12.30);
            med2.setCurrentStock(50);
            med2.setMinimumStock(10);
            med2.setExpiryDate(LocalDate.now().plusYears(1));
            med2.setRequiresPrescription(true);
            med2.setIsActive(true);
            med2.setCreatedDate(LocalDateTime.now());
            pharmacyRepository.save(med2);
            
            log.info("✅ 2 médicaments créés");
        }
    }

    private void initializeLabTests() {
        if (labTestRepository.count() == 0) {
            log.info("Création des tests de laboratoire...");
            
            // Test 1
            LabTest test1 = new LabTest();
            test1.setTestCode("LT" + System.currentTimeMillis() + "001");
            test1.setTestName("Numération Formule Sanguine");
            test1.setDescription("Analyse complète du sang");
            test1.setCategory("Hématologie");
            test1.setSampleType("Sang");
            test1.setPrice(25.0);
            test1.setProcessingTimeHours(2);
            test1.setIsActive(true);
            test1.setIsUrgentAvailable(true);
            test1.setCreatedDate(LocalDateTime.now());
            labTestRepository.save(test1);
            
            // Test 2
            LabTest test2 = new LabTest();
            test2.setTestCode("LT" + System.currentTimeMillis() + "002");
            test2.setTestName("Glycémie à jeun");
            test2.setDescription("Mesure du taux de glucose sanguin");
            test2.setCategory("Biochimie");
            test2.setSampleType("Sang");
            test2.setPrice(15.0);
            test2.setProcessingTimeHours(1);
            test2.setPreparationInstructions("Être à jeun depuis 12 heures");
            test2.setIsActive(true);
            test2.setIsUrgentAvailable(false);
            test2.setCreatedDate(LocalDateTime.now());
            labTestRepository.save(test2);
            
            log.info("✅ 2 tests de laboratoire créés");
        }
    }

    private void initializeConfiguration() {
        if (configurationService.getActiveConfiguration().isEmpty()) {
            log.info("Création de la configuration par défaut...");
            
            HospitalConfiguration config = new HospitalConfiguration();
            config.setHospitalName("Centre Hospitalier Universitaire");
            config.setHospitalAddress("123 Avenue de la Santé");
            config.setCity("Paris");
            config.setPostalCode("75014");
            config.setCountry("France");
            config.setPhone("01 42 16 00 00");
            config.setFax("01 42 16 00 01");
            config.setEmail("contact@chu-paris.fr");
            config.setWebsite("www.chu-paris.fr");
            config.setHospitalType("PUBLIC");
            config.setLicenseNumber("LIC-CHU-2024-001");
            config.setDirectorName("Dr. François MARTIN");
            config.setMedicalDirectorName("Dr. Sophie BERNARD");
            config.setTotalBeds(450);
            config.setEmergencyPhone("15");
            config.setAppointmentPhone("01 42 16 10 10");
            config.setWorkingHours("7h00 - 19h00");
            config.setEmergencyHours("24h/24 - 7j/7");
            config.setLanguage("FR");
            config.setTimezone("Europe/Paris");
            config.setCurrency("EUR");
            config.setTaxRate(0.20);
            config.setPatientNumberPrefix("P");
            config.setDoctorNumberPrefix("D");
            config.setBillNumberPrefix("BILL");
            config.setEnableSmsNotifications(false);
            config.setEnableEmailNotifications(true);
            config.setAutoBackupEnabled(true);
            config.setBackupFrequencyHours(24);
            config.setSessionTimeoutMinutes(30);
            config.setMaxLoginAttempts(5);
            config.setPatientCardTemplate("DEFAULT");
            config.setPrescriptionTemplate("DEFAULT");
            config.setCertificateTemplate("DEFAULT");
            config.setFooterText("Centre Hospitalier Universitaire - Service Public de Santé");
            config.setHeaderText("Votre santé, notre priorité");
            config.setSystemVersion("1.0.0");
            config.setMaintenanceMode(false);
            
            configurationService.saveConfiguration(config);
            log.info("✅ Configuration par défaut créée");
        }
    }
}