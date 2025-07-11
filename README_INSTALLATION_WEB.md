# 🌐 Interface Web d'Installation - Système Hospitalier

## 📋 Vue d'Ensemble

L'interface web d'installation permet de configurer facilement le système d'information hospitalier en quelques étapes simples, similaire à l'installateur de Dolibarr. Cette interface guide l'utilisateur à travers toutes les étapes nécessaires pour déployer un système hospitalier complet.

## ✨ Fonctionnalités

### 🎯 **Installation Guidée en 5 Étapes**

1. **Page d'Accueil** - Présentation du système et des fonctionnalités
2. **Vérification des Prérequis** - Contrôle de l'environnement système
3. **Configuration Base de Données** - Sélection et configuration de la DB
4. **Configuration Système** - Paramètres généraux et compte admin
5. **Installation & Finalisation** - Installation automatique et complétion

### 🔧 **Fonctionnalités Avancées**

- **Support Multi-Bases** : PostgreSQL, MySQL, H2
- **Test de Connexion** en temps réel
- **Vérification des Prérequis** automatique
- **Installation Progressive** avec feedback visuel
- **Journal d'Installation** en temps réel
- **Interface Responsive** compatible mobile/desktop
- **Sécurité Intégrée** avec validation des entrées

## 🚀 Accès à l'Installation

### **URL d'Accès**
```
http://localhost:8080/install
```

### **Démarrage Rapide**
```bash
# Démarrer l'application
mvn spring-boot:run

# Ou avec le script de démarrage
./start-hospital-system.sh

# Accéder à l'interface
# Ouvrir http://localhost:8080 dans le navigateur
# Le système redirige automatiquement vers /install
```

## 📖 Guide d'Installation Détaillé

### **Étape 1 : Page d'Accueil**
- **Présentation** du système et de ses modules
- **Informations** sur la version et les fonctionnalités
- **Temps d'installation** estimé (5-10 minutes)

### **Étape 2 : Vérification des Prérequis**
- ✅ **Version Java** (Java 17+ requis)
- ✅ **Espace Disque** (minimum 1GB)
- ✅ **Permissions** d'écriture dans le répertoire
- ✅ **Mémoire** disponible (minimum 512MB)

### **Étape 3 : Configuration Base de Données**

#### **Types Supportés :**
| Base de Données | Port | Usage Recommandé |
|----------------|------|------------------|
| **PostgreSQL** | 5432 | Production |
| **MySQL** | 3306 | Production/Test |
| **H2** | - | Développement/Démo |

#### **Configuration :**
- **Serveur** et port de la base
- **Nom de la base** de données
- **Utilisateur** et mot de passe
- **Test de connexion** automatique
- **Options avancées** (pool de connexions, timeout)

### **Étape 4 : Configuration Système**

#### **Paramètres Généraux :**
- Nom de l'hôpital
- Adresse et coordonnées
- Email principal

#### **Compte Administrateur :**
- Prénom et nom
- Nom d'utilisateur (défaut: admin)
- Email et mot de passe
- Confirmation du mot de passe

#### **Configuration Email (Optionnel) :**
- Serveur SMTP
- Port et authentification
- Utilisateur et mot de passe SMTP

#### **Options Avancées :**
- Timeout des sessions
- Taille max des uploads
- Audit des actions
- Sauvegardes automatiques

### **Étape 5 : Installation & Finalisation**

#### **Processus d'Installation :**
1. **Création Base de Données** - Tables et structure
2. **Création Schéma** - Index et contraintes
3. **Données Initiales** - Référentiels et exemples
4. **Compte Administrateur** - Premier utilisateur
5. **Configuration Système** - Fichiers et paramètres

#### **Suivi en Temps Réel :**
- **Barre de progression** globale
- **Status détaillé** par étape
- **Journal d'installation** avec timestamps
- **Gestion d'erreurs** et retry automatique

## 🏗️ Architecture Technique

### **Backend (Spring Boot)**
```
src/main/java/com/hospital/
├── controller/
│   ├── InstallController.java       # Contrôleur principal
│   └── MainController.java          # Redirection automatique
├── service/
│   └── InstallationService.java     # Logique d'installation
└── config/
    └── WebConfig.java               # Configuration web
```

### **Frontend (Thymeleaf + Bootstrap)**
```
src/main/resources/templates/install/
├── welcome.html                     # Page d'accueil
├── step1-requirements.html          # Vérification prérequis
├── step2-database.html              # Configuration DB
├── step3-system.html                # Configuration système
├── step4-install.html               # Installation
└── step5-complete.html              # Finalisation
```

### **API Endpoints**
| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/install` | GET | Page d'accueil installation |
| `/install/step{1-5}` | GET | Étapes de configuration |
| `/install/test-database` | POST | Test connexion DB |
| `/install/install-database` | POST | Installation DB |
| `/install/finalize` | POST | Finalisation |
| `/install/check-status` | GET | Statut installation |

## 🔒 Sécurité

### **Mesures de Sécurité :**
- **Validation** des entrées côté serveur
- **Protection CSRF** intégrée
- **Échappement** automatique des données
- **Connexions sécurisées** à la base
- **Mots de passe** hachés (BCrypt)
- **Sessions** sécurisées

### **Contrôles d'Accès :**
- **Installation unique** - Verrou après installation
- **Redirection automatique** si déjà installé
- **Validation** des étapes séquentielles
- **Timeout** des sessions d'installation

## 🎨 Interface Utilisateur

### **Design Moderne :**
- **Bootstrap 5** pour le responsive
- **Font Awesome** pour les icônes
- **Animations CSS** fluides
- **Gradients** et effets visuels
- **Couleurs** cohérentes avec la santé

### **Expérience Utilisateur :**
- **Navigation intuitive** entre les étapes
- **Feedback visuel** en temps réel
- **Messages d'erreur** clairs
- **Aide contextuelle** à chaque étape
- **Compatibilité** mobile/tablet

## 📊 Surveillance et Logs

### **Logs d'Installation :**
```bash
# Fichiers de logs
logs/installation.log              # Log principal
logs/database-setup.log           # Log base de données
logs/system-config.log            # Log configuration
```

### **Monitoring :**
- **Progression** en temps réel
- **Statut** de chaque composant
- **Erreurs** et diagnostics
- **Performance** et temps d'exécution

## 🔧 Configuration Avancée

### **Variables d'Environnement :**
```bash
# Configuration de l'installation
INSTALL_MODE=web                   # Mode d'installation
INSTALL_DEBUG=true                 # Debug détaillé
INSTALL_TIMEOUT=300               # Timeout en secondes
INSTALL_BACKUP=true               # Sauvegarde auto
```

### **Fichiers de Configuration :**
```yaml
# application-install.yml
install:
  web:
    enabled: true
    timeout: 300
    backup-before: true
    validate-requirements: true
  database:
    supported-types: [postgresql, mysql, h2]
    test-connection: true
    create-schema: true
```

## 🧪 Tests et Validation

### **Tests Automatisés :**
```bash
# Tests d'intégration de l'installation
mvn test -Dtest=InstallationControllerTest
mvn test -Dtest=InstallationServiceTest
```

### **Tests Manuels :**
1. **Navigation** entre toutes les étapes
2. **Validation** des formulaires
3. **Test de connexion** DB sur différents types
4. **Installation complète** avec données
5. **Gestion d'erreurs** et recovery

## 🚀 Déploiement

### **Production :**
```bash
# Build pour production
mvn clean package -Pprod

# Démarrage avec profil de production
java -jar target/hospital-system.jar --spring.profiles.active=prod
```

### **Docker :**
```dockerfile
FROM openjdk:17-jdk
COPY target/hospital-system.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📚 Documentation

### **Ressources :**
- **API Documentation** : `/hospital/api-docs`
- **Guide Utilisateur** : `/hospital/help`
- **Manuel Admin** : `GUIDE_ADMINISTRATION.md`
- **FAQ Installation** : `FAQ_INSTALLATION.md`

### **Support :**
- **Logs système** accessibles via l'interface
- **Diagnostic automatique** des problèmes
- **Guide de dépannage** intégré
- **Contact support** via l'interface

## 🎯 Prochaines Étapes

Après l'installation réussie :

1. **Première Connexion** avec le compte admin
2. **Configuration** des paramètres hospitaliers
3. **Création** des utilisateurs métier
4. **Import** des données existantes
5. **Formation** de l'équipe sur le système

---

## 🏆 Résumé

L'interface web d'installation offre une expérience utilisateur moderne et intuitive pour déployer rapidement un système d'information hospitalier complet. Avec son design responsive, ses vérifications automatiques et son installation progressive, elle garantit un déploiement réussi même pour les utilisateurs non techniques.

**🚀 Prêt à installer ? Lancez l'application et visitez http://localhost:8080 !**