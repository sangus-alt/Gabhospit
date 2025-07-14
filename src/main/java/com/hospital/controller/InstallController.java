package com.hospital.controller;

import com.hospital.service.InstallationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/install")
@RequiredArgsConstructor
@Slf4j
public class InstallController {

    private final InstallationService installationService;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * Page d'accueil de l'installation
     */
    @GetMapping
    public String installHome(Model model, HttpSession session) {
        model.addAttribute("version", appVersion);
        model.addAttribute("currentStep", 1);
        session.setAttribute("installStep", 1);
        return "install/welcome";
    }

    /**
     * Étape 1: Vérification des prérequis
     */
    @GetMapping("/step1")
    public String step1(Model model, HttpSession session) {
        Map<String, Boolean> requirements = installationService.checkRequirements();
        model.addAttribute("requirements", requirements);
        model.addAttribute("currentStep", 1);
        session.setAttribute("installStep", 1);
        return "install/step1-requirements";
    }

    /**
     * Étape 2: Configuration de la base de données
     */
    @GetMapping("/step2")
    public String step2(Model model, HttpSession session) {
        Integer currentStep = (Integer) session.getAttribute("installStep");
        if (currentStep == null || currentStep < 1) {
            return "redirect:/install/step1";
        }
        
        model.addAttribute("currentStep", 2);
        model.addAttribute("dbTypes", new String[]{"postgresql", "mysql", "h2"});
        session.setAttribute("installStep", 2);
        return "install/step2-database";
    }

    /**
     * Test de connexion à la base de données
     */
    @PostMapping("/test-database")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testDatabase(@RequestBody Map<String, String> dbConfig) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean connectionOk = installationService.testDatabaseConnection(
                dbConfig.get("host"),
                dbConfig.get("port"),
                dbConfig.get("database"),
                dbConfig.get("username"),
                dbConfig.get("password"),
                dbConfig.get("type")
            );
            
            result.put("success", connectionOk);
            result.put("message", connectionOk ? "Connexion réussie!" : "Échec de la connexion");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Étape 3: Configuration du système
     */
    @GetMapping("/step3")
    public String step3(Model model, HttpSession session) {
        Integer currentStep = (Integer) session.getAttribute("installStep");
        if (currentStep == null || currentStep < 2) {
            return "redirect:/install/step2";
        }
        
        model.addAttribute("currentStep", 3);
        session.setAttribute("installStep", 3);
        return "install/step3-system";
    }

    /**
     * Étape 4: Création des tables et données initiales
     */
    @GetMapping("/step4")
    public String step4(Model model, HttpSession session) {
        Integer currentStep = (Integer) session.getAttribute("installStep");
        if (currentStep == null || currentStep < 3) {
            return "redirect:/install/step3";
        }
        
        model.addAttribute("currentStep", 4);
        session.setAttribute("installStep", 4);
        return "install/step4-install";
    }

    /**
     * Installation de la base de données
     */
    @PostMapping("/install-database")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> installDatabase(
            @RequestBody Map<String, String> config, 
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Sauvegarder la configuration dans la session
            session.setAttribute("dbConfig", config);
            
            // Créer les tables
            boolean tablesCreated = installationService.createTables(config);
            if (!tablesCreated) {
                result.put("success", false);
                result.put("message", "Échec de la création des tables");
                return ResponseEntity.ok(result);
            }
            
            // Insérer les données initiales
            boolean dataInserted = installationService.insertInitialData(config);
            if (!dataInserted) {
                result.put("success", false);
                result.put("message", "Échec de l'insertion des données initiales");
                return ResponseEntity.ok(result);
            }
            
            // Créer le fichier de configuration
            boolean configCreated = installationService.createConfigurationFile(config);
            if (!configCreated) {
                result.put("success", false);
                result.put("message", "Échec de la création du fichier de configuration");
                return ResponseEntity.ok(result);
            }
            
            result.put("success", true);
            result.put("message", "Installation réussie!");
            session.setAttribute("installStep", 5);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'installation: ", e);
            result.put("success", false);
            result.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Étape 5: Finalisation
     */
    @GetMapping("/step5")
    public String step5(Model model, HttpSession session) {
        Integer currentStep = (Integer) session.getAttribute("installStep");
        if (currentStep == null || currentStep < 5) {
            return "redirect:/install/step4";
        }
        
        model.addAttribute("currentStep", 5);
        return "install/step5-complete";
    }

    /**
     * Finaliser l'installation
     */
    @PostMapping("/finalize")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> finalizeInstallation(
            @RequestBody Map<String, String> adminConfig,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Créer l'administrateur
            @SuppressWarnings("unchecked")
            Map<String, String> dbConfig = (Map<String, String>) session.getAttribute("dbConfig");
            
            boolean adminCreated = installationService.createAdminUser(
                adminConfig.get("username"),
                adminConfig.get("password"),
                adminConfig.get("email"),
                adminConfig.get("firstName"),
                adminConfig.get("lastName"),
                dbConfig
            );
            
            if (!adminCreated) {
                result.put("success", false);
                result.put("message", "Échec de la création de l'administrateur");
                return ResponseEntity.ok(result);
            }
            
            // Marquer l'installation comme terminée
            installationService.markInstallationComplete();
            
            result.put("success", true);
            result.put("message", "Installation terminée avec succès!");
            result.put("redirectUrl", "/login");
            
        } catch (Exception e) {
            log.error("Erreur lors de la finalisation: ", e);
            result.put("success", false);
            result.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Vérifier si l'installation est déjà terminée
     */
    @GetMapping("/check-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkInstallationStatus() {
        Map<String, Object> result = new HashMap<>();
        boolean isInstalled = installationService.isInstallationComplete();
        result.put("installed", isInstalled);
        return ResponseEntity.ok(result);
    }
}