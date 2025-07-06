# 🏥 SYSTÈME D'INFORMATION HOSPITALIER (SIH) - TUTORIEL COMPLET

## 📋 TABLE DES MATIÈRES

1. [Introduction](#introduction)
2. [Installation et Configuration](#installation-et-configuration)
3. [Première Connexion](#première-connexion)
4. [Modules Principaux](#modules-principaux)
5. [Guide d'Utilisation par Module](#guide-dutilisation-par-module)
6. [Architecture Technique](#architecture-technique)
7. [Sécurité et Conformité](#sécurité-et-conformité)
8. [Maintenance et Support](#maintenance-et-support)

## 🎯 INTRODUCTION

Le Système d'Information Hospitalier (SIH) est une solution complète de gestion hospitalière conçue pour les hôpitaux européens de grande taille. Il intègre tous les aspects de la gestion hospitalière moderne avec une architecture robuste basée sur Java Spring Boot.

### 🔧 TECHNOLOGIES UTILISÉES

- **Backend**: Java 17, Spring Boot 3.2.0, Spring Data JPA, Spring Security
- **Base de données**: PostgreSQL (production), H2 (développement)
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap 5, Chart.js
- **Sécurité**: JWT, Authentification multi-niveaux, Chiffrement des données
- **API**: RESTful avec documentation Swagger/OpenAPI

### 📊 MODULES INTÉGRÉS

1. **GESTION PATIENTS** - Dossier médical électronique complet
2. **CONSULTATIONS SPÉCIALISÉES** - Toutes les spécialités médicales
3. **SYSTÈME LIMS AVANCÉ** - Gestion complète du laboratoire
4. **SYSTÈME RIS AVANCÉ** - Radiologie et imagerie médicale
5. **HOSPITALISATION COMPLÈTE** - Gestion des lits et séjours
6. **FACTURATION ET CAISSE** - Gestion financière complète
7. **PHARMACIE INTÉGRÉE** - Gestion des médicaments et stocks
8. **FILE D'ATTENTE INTELLIGENTE** - Optimisation des flux patients
9. **GESTION VACCINS** - Suivi des vaccinations
10. **GESTION ASSURANCES AVANCÉE** - Interface avec les assureurs
11. **GESTION STOCKS MATÉRIEL** - Inventaire hospitalier
12. **CERTIFICATS MÉDICAUX** - Génération automatisée
13. **RAPPORTS ET STATISTIQUES** - Tableaux de bord dynamiques
14. **CONFIGURATION SYSTÈME** - Paramétrage avancé

## 🚀 INSTALLATION ET CONFIGURATION

### Prérequis Système

```bash
# Versions minimales requises
- Java 17 ou supérieur
- PostgreSQL 13 ou supérieur
- Maven 3.8 ou supérieur
- 8 GB RAM minimum (16 GB recommandé)
- 100 GB d'espace disque minimum
```

### Installation Étape par Étape

1. **Cloner le projet**
```bash
git clone https://github.com/votre-repo/hospital-management-system.git
cd hospital-management-system
```

2. **Configuration de la base de données**
```sql
-- Créer la base de données PostgreSQL
CREATE DATABASE hospital_db;
CREATE USER hospital_user WITH PASSWORD 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON DATABASE hospital_db TO hospital_user;
```

3. **Configuration application.yml**
```yaml
# Modifier src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hospital_db
    username: hospital_user
    password: votre_mot_de_passe
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  security:
    jwt:
      secret-key: "votre_clé_secrète_très_longue_et_sécurisée"
      expiration-time: 86400000 # 24 heures
  
server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.hospital: DEBUG
    org.springframework.security: DEBUG
```

4. **Compilation et démarrage**
```bash
# Compiler le projet
mvn clean install

# Démarrer l'application
mvn spring-boot:run
```

5. **Vérification de l'installation**
```bash
# Vérifier que l'application démarre
curl http://localhost:8080/api/health

# Accéder à la documentation API
# Ouvrir http://localhost:8080/api/swagger-ui.html
```

## 🔐 PREMIÈRE CONNEXION

### Compte Administrateur par Défaut

Au premier démarrage, le système crée automatiquement un compte administrateur :

```
🔑 INFORMATIONS DE CONNEXION ADMINISTRATEUR
URL: http://localhost:8080
Nom d'utilisateur: admin
Mot de passe: Admin123!
Email: admin@hospital.com

⚠️ ATTENTION: Changez immédiatement ce mot de passe!
```

### Première Configuration

1. **Connexion initiale**
   - Ouvrir http://localhost:8080/admin-dashboard.html
   - Saisir les identifiants par défaut
   - Le système forcera le changement de mot de passe

2. **Configuration initiale obligatoire**
   - Modifier le mot de passe administrateur
   - Configurer les paramètres de l'hôpital
   - Créer les premiers utilisateurs
   - Paramétrer les départements

3. **Vérification de sécurité**
   - Test des permissions utilisateur
   - Vérification des sauvegardes automatiques
   - Configuration des notifications

## 📋 MODULES PRINCIPAUX

### 1. 👥 GESTION PATIENTS

**Fonctionnalités clés :**
- Numéro patient unique automatique (Format: P{timestamp}{uuid})
- Dossier médical électronique complet
- Historique médical détaillé
- Gestion des contacts d'urgence
- Allergies et contre-indications
- Photos et documents

**Utilisation :**
```javascript
// Création d'un nouveau patient
POST /api/patients
{
  "firstName": "Marie",
  "lastName": "Dubois",
  "dateOfBirth": "1985-03-15",
  "gender": "FEMALE",
  "phone": "0123456789",
  "email": "marie.dubois@email.com",
  "address": "123 Rue de la Paix, 75001 Paris",
  "socialSecurityNumber": "1850315123456",
  "emergencyContact": {
    "name": "Jean Dubois",
    "phone": "0987654321",
    "relationship": "Époux"
  }
}
```

### 2. 🩺 CONSULTATIONS SPÉCIALISÉES

**Spécialités disponibles :**
- Cardiologie
- Pneumologie
- Gastroentérologie
- Neurologie
- Orthopédie
- Gynécologie
- Pédiatrie
- Dermatologie
- Ophtalmologie
- ORL
- Psychiatrie
- Oncologie

**Processus de consultation :**
1. Prise de rendez-vous
2. Arrivée et file d'attente
3. Consultation médicale
4. Prescriptions et ordonnances
5. Suivi post-consultation

### 3. 🔬 SYSTÈME LIMS (Laboratory Information Management System)

**Fonctionnalités avancées :**
- Gestion complète des analyses
- Suivi des échantillons
- Validation des résultats
- Interfaçage avec les automates
- Contrôle qualité
- Traçabilité complète

**Workflow type :**
```
Prescription → Prélèvement → Analyse → Validation → Résultats → Archivage
```

### 4. 📡 SYSTÈME RIS (Radiology Information System)

**Examens supportés :**
- Radiographie standard
- Scanner (CT)
- IRM
- Échographie
- Mammographie
- Médecine nucléaire

**Processus d'imagerie :**
1. Prescription médicale
2. Planification de l'examen
3. Réalisation de l'imagerie
4. Stockage DICOM
5. Lecture et interprétation
6. Rapport radiologique

### 5. 🏥 HOSPITALISATION COMPLÈTE

**Gestion des lits :**
- Disponibilité en temps réel
- Réservation et attribution
- Nettoyage et maintenance
- Optimisation d'occupation

**Suivi des patients hospitalisés :**
- Admissions/sorties
- Transferts entre services
- Soins quotidiens
- Surveillance médicale

### 6. 💰 FACTURATION ET CAISSE

**Fonctionnalités financières :**
- Facturation automatique
- Gestion des tarifs
- Interface avec les assurances
- Comptabilité intégrée
- Reporting financier

**Types de facturation :**
- Consultations
- Examens
- Hospitalisation
- Médicaments
- Matériel médical

### 7. 💊 PHARMACIE INTÉGRÉE

**Gestion des médicaments :**
- Inventaire en temps réel
- Gestion des stocks
- Péremption automatique
- Commandes fournisseurs
- Dispensation sécurisée

**Sécurité pharmaceutique :**
- Vérification des interactions
- Contre-indications
- Allergies patients
- Traçabilité complète

### 8. 📊 FILE D'ATTENTE INTELLIGENTE

**Optimisation des flux :**
- Prise de numéro automatique
- Estimation des temps d'attente
- Notifications SMS/email
- Priorisation médicale
- Gestion multi-services

**Interface patient :**
- Écrans d'affichage
- Application mobile
- Rappels automatiques
- Satisfaction client

### 9. 💉 GESTION VACCINS

**Suivi vaccinal :**
- Calendrier vaccinal
- Rappels automatiques
- Certificats de vaccination
- Gestion des lots
- Effets indésirables

**Types de vaccins :**
- Vaccins obligatoires
- Vaccins recommandés
- Vaccins de voyage
- Vaccins professionnels

### 10. 🛡️ GESTION ASSURANCES AVANCÉE

**Interface assureurs :**
- Vérification d'éligibilité
- Pré-autorisation
- Transmission des factures
- Suivi des remboursements
- Tiers payant

**Types d'assurance :**
- Sécurité sociale
- Mutuelles
- Assurances privées
- Assurances internationales

## 🔧 ARCHITECTURE TECHNIQUE

### Structure du Projet

```
hospital-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hospital/
│   │   │       ├── entity/          # Entités JPA
│   │   │       ├── repository/      # Couche d'accès aux données
│   │   │       ├── service/         # Logique métier
│   │   │       ├── controller/      # Contrôleurs REST
│   │   │       ├── config/          # Configuration Spring
│   │   │       ├── security/        # Sécurité et authentification
│   │   │       └── util/            # Utilitaires
│   │   └── resources/
│   │       ├── application.yml      # Configuration
│   │       ├── static/              # Fichiers statiques
│   │       └── templates/           # Templates Thymeleaf
│   └── test/                        # Tests unitaires
├── pom.xml                          # Configuration Maven
└── README.md                        # Documentation
```

### Base de Données

**Principales tables :**
- `patients` - Informations patients
- `doctors` - Médecins et praticiens
- `appointments` - Rendez-vous
- `medical_records` - Dossiers médicaux
- `prescriptions` - Ordonnances
- `lab_tests` - Tests laboratoire
- `imaging_results` - Résultats d'imagerie
- `bills` - Factures
- `insurances` - Assurances
- `users` - Utilisateurs système
- `roles` - Rôles et permissions

### API REST

**Endpoints principaux :**
```
GET    /api/patients           # Liste des patients
POST   /api/patients           # Créer un patient
GET    /api/patients/{id}      # Détails d'un patient
PUT    /api/patients/{id}      # Modifier un patient
DELETE /api/patients/{id}      # Supprimer un patient

GET    /api/appointments       # Liste des rendez-vous
POST   /api/appointments       # Créer un rendez-vous
GET    /api/appointments/{id}  # Détails d'un rendez-vous

GET    /api/lab-tests          # Tests laboratoire
POST   /api/lab-tests          # Créer un test
GET    /api/lab-results        # Résultats de laboratoire

GET    /api/queue              # File d'attente
POST   /api/queue              # Ajouter à la file
PUT    /api/queue/{id}/call    # Appeler un patient

GET    /api/reports            # Rapports
GET    /api/statistics         # Statistiques
```

## 🛡️ SÉCURITÉ ET CONFORMITÉ

### Sécurité

**Authentification :**
- JWT (JSON Web Tokens)
- Authentification multi-facteurs
- Gestion des sessions
- Verrouillage automatique des comptes

**Autorisation :**
- Rôles et permissions granulaires
- Contrôle d'accès basé sur les rôles (RBAC)
- Audit trail complet
- Logs de sécurité

**Chiffrement :**
- Données sensibles chiffrées
- Communications HTTPS
- Mots de passe hashés (BCrypt)
- Clés de chiffrement rotatives

### Conformité RGPD

**Protection des données :**
- Consentement explicite
- Droit à l'oubli
- Portabilité des données
- Notification des violations

**Audit et traçabilité :**
- Logs détaillés
- Historique des modifications
- Accès aux données tracé
- Rapports de conformité

## 🔧 MAINTENANCE ET SUPPORT

### Sauvegarde

**Sauvegarde automatique :**
```bash
# Configuration de sauvegarde quotidienne
# Crontab entry
0 2 * * * /opt/hospital/backup-script.sh
```

**Stratégie de sauvegarde :**
- Sauvegarde quotidienne complète
- Sauvegarde incrémentale toutes les 6 heures
- Rétention 30 jours
- Stockage sécurisé hors site

### Monitoring

**Métriques surveillées :**
- Performance de l'application
- Utilisation des ressources
- Erreurs et exceptions
- Temps de réponse API

**Alertes automatiques :**
- Panne système
- Pic d'utilisation
- Erreurs critiques
- Espace disque faible

### Support Technique

**Niveaux de support :**
1. **Support utilisateur** - Formation et assistance
2. **Support technique** - Problèmes système
3. **Support développement** - Évolutions et bugs
4. **Support infrastructure** - Serveurs et réseau

**Canaux de support :**
- Hotline téléphonique
- Support email
- Chat en ligne
- Documentation en ligne

## 📞 CONTACT ET SUPPORT

**Équipe de développement :**
- Email: support@hospital-system.com
- Téléphone: +33 1 23 45 67 89
- Documentation: https://docs.hospital-system.com

**Support d'urgence 24/7 :**
- Téléphone: +33 1 23 45 67 90
- Email: urgence@hospital-system.com

---

## 🔄 MISES À JOUR ET ÉVOLUTIONS

Ce système est en développement continu. Consultez régulièrement la documentation pour les nouvelles fonctionnalités et mises à jour de sécurité.

**Dernière mise à jour :** Décembre 2024
**Version :** 1.0.0
**Statut :** Production Ready

---

*Ce tutoriel couvre les fonctionnalités principales du système. Pour des informations plus détaillées, consultez la documentation API et les guides spécialisés par module.*