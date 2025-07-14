package com.hospital;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.entity.*;
import com.hospital.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class HospitalSystemIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPatientManagementFlow() throws Exception {
        // Test création d'un patient
        Patient patient = new Patient();
        patient.setFirstName("Jean");
        patient.setLastName("Dupont");
        patient.setDateOfBirth(LocalDate.of(1985, 5, 15));
        patient.setPhoneNumber("0123456789");
        patient.setEmail("jean.dupont@email.com");
        patient.setGender("M");
        patient.setAddress("123 Rue de la Paix, Paris");
        patient.setEmergencyContact("Marie Dupont - 0987654321");
        patient.setBloodType("O+");

        String patientJson = objectMapper.writeValueAsString(patient);

        // Créer le patient
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"));

        // Vérifier la liste des patients
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // Rechercher par nom
        mockMvc.perform(get("/api/patients/search")
                .param("firstName", "Jean"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDoctorAndAppointmentFlow() throws Exception {
        // Créer un département
        Department department = new Department();
        department.setName("Cardiologie");
        department.setDescription("Service de cardiologie");
        department = departmentRepository.save(department);

        // Créer un médecin
        Doctor doctor = new Doctor();
        doctor.setFirstName("Dr. Marie");
        doctor.setLastName("Martin");
        doctor.setSpecialty("Cardiologue");
        doctor.setLicenseNumber("MD123456");
        doctor.setPhoneNumber("0123456789");
        doctor.setEmail("dr.martin@hospital.com");
        doctor.setDepartment(department);

        String doctorJson = objectMapper.writeValueAsString(doctor);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(doctorJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.firstName").value("Dr. Marie"));

        // Test récupération des médecins
        mockMvc.perform(get("/api/doctors"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "BILLING_STAFF")
    void testBillingFlow() throws Exception {
        // Créer un patient pour la facturation
        Patient patient = createTestPatient();
        patient = patientRepository.save(patient);

        // Créer une facture
        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setBillDate(LocalDate.now());
        bill.setDueDate(LocalDate.now().plusDays(30));
        bill.setTotalAmount(BigDecimal.valueOf(150.00));
        bill.setStatus("PENDING");

        String billJson = objectMapper.writeValueAsString(bill);

        mockMvc.perform(post("/api/billing/bills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(billJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.totalAmount").value(150.00));

        // Test récupération des factures
        mockMvc.perform(get("/api/billing/bills"))
                .andExpected(status().isOk());

        // Test génération PDF
        mockMvc.perform(get("/api/billing/bills/1/pdf"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @WithMockUser(roles = "LAB_TECHNICIAN")
    void testLaboratoryFlow() throws Exception {
        // Créer un test de laboratoire
        LabTest labTest = new LabTest();
        labTest.setName("Hémogramme Complet");
        labTest.setCode("CBC");
        labTest.setCategory("Hématologie");
        labTest.setPrice(BigDecimal.valueOf(50.00));
        labTest.setNormalRange("4.5-11.0 x10^9/L");

        String labTestJson = objectMapper.writeValueAsString(labTest);

        mockMvc.perform(post("/api/laboratory/tests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(labTestJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.name").value("Hémogramme Complet"));

        // Test récupération des tests
        mockMvc.perform(get("/api/laboratory/tests"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "NURSE")
    void testVaccineFlow() throws Exception {
        // Créer un vaccin
        Vaccine vaccine = new Vaccine();
        vaccine.setName("COVID-19");
        vaccine.setManufacturer("Pfizer");
        vaccine.setLotNumber("LOT123");
        vaccine.setExpiryDate(LocalDate.now().plusYears(1));
        vaccine.setQuantityInStock(100);
        vaccine.setMinimumStockLevel(10);
        vaccine.setDoseVolume(BigDecimal.valueOf(0.5));
        vaccine.setStorageTemperature("-70°C");

        String vaccineJson = objectMapper.writeValueAsString(vaccine);

        mockMvc.perform(post("/api/vaccines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(vaccineJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.name").value("COVID-19"));

        // Test vérification stock faible
        mockMvc.perform(get("/api/vaccines/low-stock"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INVENTORY_MANAGER")
    void testInventoryFlow() throws Exception {
        // Créer un article d'inventaire
        InventoryItem item = new InventoryItem();
        item.setName("Gants médicaux");
        item.setCategory("Consommables");
        item.setBarcode("123456789");
        item.setQuantityInStock(500);
        item.setMinimumStockLevel(50);
        item.setUnitPrice(BigDecimal.valueOf(0.50));
        item.setLocation("Magasin principal");

        String itemJson = objectMapper.writeValueAsString(item);

        mockMvc.perform(post("/api/inventory/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.name").value("Gants médicaux"));

        // Test opération de stock
        mockMvc.perform(post("/api/inventory/items/1/issue")
                .param("quantity", "10")
                .param("issuedTo", "Service urgences")
                .param("purpose", "Consultation")
                .param("issuedBy", "Infirmière Chef"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RADIOLOGIST")
    void testImagingFlow() throws Exception {
        // Créer un patient
        Patient patient = createTestPatient();
        patient = patientRepository.save(patient);

        // Créer un résultat d'imagerie
        ImagingResult result = new ImagingResult();
        result.setPatient(patient);
        result.setExamType("Radiographie thoracique");
        result.setOrderDate(LocalDate.now());
        result.setStatus("ORDERED");
        result.setPriority("NORMAL");
        result.setClinicalHistory("Douleur thoracique");

        String resultJson = objectMapper.writeValueAsString(result);

        mockMvc.perform(post("/api/imaging/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resultJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.examType").value("Radiographie thoracique"));

        // Test démarrage d'examen
        mockMvc.perform(post("/api/imaging/results/1/start-exam")
                .param("technician", "Tech. Radios"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BILLING_STAFF")
    void testInsuranceFlow() throws Exception {
        // Créer une compagnie d'assurance
        Insurance insurance = new Insurance();
        insurance.setName("Assurance Santé Plus");
        insurance.setType("PRIVATE");
        insurance.setContactEmail("contact@assurancesante.com");
        insurance.setContactPhone("0123456789");
        insurance.setAddress("123 Boulevard des Assurances");
        insurance.setActive(true);

        String insuranceJson = objectMapper.writeValueAsString(insurance);

        mockMvc.perform(post("/api/insurance/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(insuranceJson))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.name").value("Assurance Santé Plus"));

        // Test vérification d'éligibilité
        mockMvc.perform(post("/api/insurance/verify-eligibility")
                .param("patientId", "1")
                .param("insuranceId", "1")
                .param("serviceCode", "CONSULTATION"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDashboardStatistics() throws Exception {
        // Test statistiques du tableau de bord
        mockMvc.perform(get("/api/dashboard/statistics"))
                .andExpected(status().isOk());

        // Test revenus mensuels
        mockMvc.perform(get("/api/billing/statistics/revenue")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-01-31"))
                .andExpected(status().isOk());

        // Test statistiques patients
        mockMvc.perform(get("/api/patients/statistics"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReportsGeneration() throws Exception {
        // Test génération rapport quotidien
        mockMvc.perform(get("/api/billing/reports/daily")
                .param("date", "2024-01-15"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_PDF));

        // Test rapport laboratoire
        mockMvc.perform(get("/api/laboratory/quality-control")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-01-31"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testQueueManagement() throws Exception {
        // Test gestion file d'attente
        mockMvc.perform(get("/api/queue/current"))
                .andExpected(status().isOk());

        // Test ajout à la file d'attente
        mockMvc.perform(post("/api/queue/add")
                .param("patientId", "1")
                .param("serviceType", "CONSULTATION")
                .param("priority", "NORMAL"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPrintServices() throws Exception {
        // Test impression étiquettes patient
        mockMvc.perform(get("/api/print/patient-labels/1"))
                .andExpected(status().isOk());

        // Test impression ordonnance
        mockMvc.perform(get("/api/print/prescription/1"))
                .andExpected(status().isOk());
    }

    @Test
    void testApiDocumentation() throws Exception {
        // Test accès à la documentation Swagger
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpected(status().isOk());

        // Test accès aux spécifications OpenAPI
        mockMvc.perform(get("/v3/api-docs"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testErrorHandling() throws Exception {
        // Test gestion erreur - ressource non trouvée
        mockMvc.perform(get("/api/patients/99999"))
                .andExpected(status().isNotFound());

        // Test validation - données invalides
        Patient invalidPatient = new Patient();
        // Pas de nom/prénom requis
        String invalidJson = objectMapper.writeValueAsString(invalidPatient);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpected(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSystemConfiguration() throws Exception {
        // Test configuration hôpital
        mockMvc.perform(get("/api/hospital-config"))
                .andExpected(status().isOk());

        // Test mise à jour configuration
        mockMvc.perform(put("/api/hospital-config/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchAndFiltering() throws Exception {
        // Test recherche patients avec filtres
        mockMvc.perform(get("/api/patients")
                .param("firstName", "Jean")
                .param("gender", "M")
                .param("page", "0")
                .param("size", "10"))
                .andExpected(status().isOk());

        // Test recherche factures avec dates
        mockMvc.perform(get("/api/billing/bills")
                .param("fromDate", "2024-01-01")
                .param("toDate", "2024-01-31")
                .param("status", "PENDING"))
                .andExpected(status().isOk());
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setFirstName("Test");
        patient.setLastName("Patient");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setPhoneNumber("0123456789");
        patient.setEmail("test@patient.com");
        patient.setGender("M");
        patient.setAddress("123 Test Street");
        patient.setEmergencyContact("Test Contact");
        patient.setBloodType("A+");
        return patient;
    }
}