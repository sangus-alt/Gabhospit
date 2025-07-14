package com.hospital.controller;

import com.hospital.service.InstallationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final InstallationService installationService;

    /**
     * Page d'accueil qui redirige vers l'installation ou le système
     */
    @GetMapping("/")
    public String home() {
        if (installationService.isInstallationComplete()) {
            return "redirect:/hospital";
        } else {
            return "redirect:/install";
        }
    }

    /**
     * Page d'accueil du système hospitalier (après installation)
     */
    @GetMapping("/hospital")
    public String hospital() {
        if (!installationService.isInstallationComplete()) {
            return "redirect:/install";
        }
        return "redirect:/hospital/dashboard";
    }
}