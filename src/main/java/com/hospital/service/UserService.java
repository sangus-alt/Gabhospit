package com.hospital.service;

import com.hospital.entity.User;
import com.hospital.entity.Role;
import com.hospital.entity.Permission;
import com.hospital.repository.UserRepository;
import com.hospital.repository.RoleRepository;
import com.hospital.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeSystem() {
        createSystemRolesAndPermissions();
        createDefaultAdminUser();
    }

    private void createSystemRolesAndPermissions() {
        // Créer les permissions système
        createPermissionIfNotExists("USER_READ", "Lecture des utilisateurs", "Utilisateurs", "READ");
        createPermissionIfNotExists("USER_WRITE", "Écriture des utilisateurs", "Utilisateurs", "WRITE");
        createPermissionIfNotExists("USER_DELETE", "Suppression des utilisateurs", "Utilisateurs", "DELETE");
        createPermissionIfNotExists("PATIENT_READ", "Lecture des patients", "Patients", "READ");
        createPermissionIfNotExists("PATIENT_WRITE", "Écriture des patients", "Patients", "WRITE");
        createPermissionIfNotExists("PATIENT_DELETE", "Suppression des patients", "Patients", "DELETE");
        createPermissionIfNotExists("DOCTOR_READ", "Lecture des médecins", "Médecins", "READ");
        createPermissionIfNotExists("DOCTOR_WRITE", "Écriture des médecins", "Médecins", "WRITE");
        createPermissionIfNotExists("APPOINTMENT_READ", "Lecture des rendez-vous", "Rendez-vous", "READ");
        createPermissionIfNotExists("APPOINTMENT_WRITE", "Écriture des rendez-vous", "Rendez-vous", "WRITE");
        createPermissionIfNotExists("BILLING_READ", "Lecture de la facturation", "Facturation", "READ");
        createPermissionIfNotExists("BILLING_WRITE", "Écriture de la facturation", "Facturation", "WRITE");
        createPermissionIfNotExists("INVENTORY_READ", "Lecture des stocks", "Stocks", "READ");
        createPermissionIfNotExists("INVENTORY_WRITE", "Écriture des stocks", "Stocks", "WRITE");
        createPermissionIfNotExists("PHARMACY_READ", "Lecture de la pharmacie", "Pharmacie", "READ");
        createPermissionIfNotExists("PHARMACY_WRITE", "Écriture de la pharmacie", "Pharmacie", "WRITE");
        createPermissionIfNotExists("LAB_READ", "Lecture du laboratoire", "Laboratoire", "READ");
        createPermissionIfNotExists("LAB_WRITE", "Écriture du laboratoire", "Laboratoire", "WRITE");
        createPermissionIfNotExists("QUEUE_READ", "Lecture des files d'attente", "Files d'attente", "READ");
        createPermissionIfNotExists("QUEUE_WRITE", "Écriture des files d'attente", "Files d'attente", "WRITE");
        createPermissionIfNotExists("VACCINE_READ", "Lecture des vaccins", "Vaccins", "READ");
        createPermissionIfNotExists("VACCINE_WRITE", "Écriture des vaccins", "Vaccins", "WRITE");
        createPermissionIfNotExists("INSURANCE_READ", "Lecture des assurances", "Assurances", "READ");
        createPermissionIfNotExists("INSURANCE_WRITE", "Écriture des assurances", "Assurances", "WRITE");
        createPermissionIfNotExists("CERTIFICATE_READ", "Lecture des certificats", "Certificats", "READ");
        createPermissionIfNotExists("CERTIFICATE_WRITE", "Écriture des certificats", "Certificats", "WRITE");
        createPermissionIfNotExists("REPORTS_READ", "Lecture des rapports", "Rapports", "READ");
        createPermissionIfNotExists("REPORTS_WRITE", "Écriture des rapports", "Rapports", "WRITE");
        createPermissionIfNotExists("SYSTEM_ADMIN", "Administration système", "Système", "ADMIN");

        // Créer les rôles système
        createRoleIfNotExists("ADMIN", "Administrateur", "Administrateur système avec tous les droits");
        createRoleIfNotExists("DOCTOR", "Médecin", "Médecin avec accès aux patients et consultations");
        createRoleIfNotExists("NURSE", "Infirmier", "Infirmier avec accès aux soins et médicaments");
        createRoleIfNotExists("PHARMACIST", "Pharmacien", "Pharmacien avec accès à la pharmacie");
        createRoleIfNotExists("LAB_TECH", "Technicien laboratoire", "Technicien de laboratoire");
        createRoleIfNotExists("RECEPTIONIST", "Réceptionniste", "Réceptionniste avec accès aux rendez-vous");
        createRoleIfNotExists("BILLING_CLERK", "Employé facturation", "Employé de facturation");
        createRoleIfNotExists("INVENTORY_MANAGER", "Gestionnaire stocks", "Gestionnaire des stocks");

        // Assigner les permissions aux rôles
        assignPermissionsToRole("ADMIN", getAllPermissions());
        assignPermissionsToRole("DOCTOR", Set.of("PATIENT_READ", "PATIENT_WRITE", "APPOINTMENT_READ", 
                                                "APPOINTMENT_WRITE", "CERTIFICATE_READ", "CERTIFICATE_WRITE",
                                                "LAB_READ", "LAB_WRITE", "PHARMACY_READ"));
        assignPermissionsToRole("NURSE", Set.of("PATIENT_READ", "PATIENT_WRITE", "APPOINTMENT_READ",
                                              "PHARMACY_READ", "VACCINE_READ", "VACCINE_WRITE"));
        assignPermissionsToRole("PHARMACIST", Set.of("PATIENT_READ", "PHARMACY_READ", "PHARMACY_WRITE",
                                                    "INVENTORY_READ", "INVENTORY_WRITE"));
        assignPermissionsToRole("LAB_TECH", Set.of("PATIENT_READ", "LAB_READ", "LAB_WRITE"));
        assignPermissionsToRole("RECEPTIONIST", Set.of("PATIENT_READ", "PATIENT_WRITE", "APPOINTMENT_READ",
                                                      "APPOINTMENT_WRITE", "QUEUE_READ", "QUEUE_WRITE"));
        assignPermissionsToRole("BILLING_CLERK", Set.of("PATIENT_READ", "BILLING_READ", "BILLING_WRITE",
                                                       "INSURANCE_READ", "INSURANCE_WRITE"));
        assignPermissionsToRole("INVENTORY_MANAGER", Set.of("INVENTORY_READ", "INVENTORY_WRITE"));
    }

    private void createDefaultAdminUser() {
        if (!userRepository.findByUsername("admin").isPresent()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@hospital.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setFirstName("Administrateur");
            admin.setLastName("Système");
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setLastLogin(LocalDateTime.now());
            admin.setMustChangePassword(true);
            
            // Assigner le rôle admin
            Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
            if (adminRole != null) {
                admin.setRoles(Set.of(adminRole));
            }
            
            userRepository.save(admin);
            
            System.out.println("=== COMPTE ADMINISTRATEUR CRÉÉ ===");
            System.out.println("Nom d'utilisateur: admin");
            System.out.println("Mot de passe: Admin123!");
            System.out.println("Email: admin@hospital.com");
            System.out.println("ATTENTION: Changez ce mot de passe dès la première connexion!");
            System.out.println("===============================");
        }
    }

    private void createPermissionIfNotExists(String name, String displayName, String module, String action) {
        if (!permissionRepository.findByName(name).isPresent()) {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setDisplayName(displayName);
            permission.setModule(module);
            permission.setAction(action);
            permission.setIsSystemPermission(true);
            permissionRepository.save(permission);
        }
    }

    private void createRoleIfNotExists(String name, String displayName, String description) {
        if (!roleRepository.findByName(name).isPresent()) {
            Role role = new Role();
            role.setName(name);
            role.setDisplayName(displayName);
            role.setDescription(description);
            role.setIsSystemRole(true);
            roleRepository.save(role);
        }
    }

    private Set<String> getAllPermissions() {
        return Set.of("USER_READ", "USER_WRITE", "USER_DELETE", "PATIENT_READ", "PATIENT_WRITE", 
                     "PATIENT_DELETE", "DOCTOR_READ", "DOCTOR_WRITE", "APPOINTMENT_READ", 
                     "APPOINTMENT_WRITE", "BILLING_READ", "BILLING_WRITE", "INVENTORY_READ", 
                     "INVENTORY_WRITE", "PHARMACY_READ", "PHARMACY_WRITE", "LAB_READ", 
                     "LAB_WRITE", "QUEUE_READ", "QUEUE_WRITE", "VACCINE_READ", "VACCINE_WRITE", 
                     "INSURANCE_READ", "INSURANCE_WRITE", "CERTIFICATE_READ", "CERTIFICATE_WRITE", 
                     "REPORTS_READ", "REPORTS_WRITE", "SYSTEM_ADMIN");
    }

    private void assignPermissionsToRole(String roleName, Set<String> permissionNames) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role != null) {
            Set<Permission> permissions = new HashSet<>();
            for (String permissionName : permissionNames) {
                Permission permission = permissionRepository.findByName(permissionName).orElse(null);
                if (permission != null) {
                    permissions.add(permission);
                }
            }
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setMustChangePassword(true);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setPasswordChangedAt(LocalDateTime.now());
                user.setMustChangePassword(false);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public void updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            user.setLoginCount(user.getLoginCount() + 1);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }

    public void incrementFailedLoginAttempts(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            
            // Verrouiller le compte après 5 tentatives échouées
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountLockedUntil(LocalDateTime.now().plusHours(1));
            }
            
            userRepository.save(user);
        }
    }

    public boolean isAccountLocked(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getAccountLockedUntil() != null && 
                   user.getAccountLockedUntil().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public void unlockAccount(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountLockedUntil(null);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            query, query, query, query);
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoles_Name(roleName);
    }

    public List<User> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getActiveUsers() {
        return userRepository.countByStatus(User.UserStatus.ACTIVE);
    }

    public long getInactiveUsers() {
        return userRepository.countByStatus(User.UserStatus.INACTIVE);
    }

    public long getLockedUsers() {
        return userRepository.countByAccountLockedUntilIsNotNull();
    }
}