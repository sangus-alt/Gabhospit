# Système d'Information Hospitalier (SIH)

## 🏥 Description

Le Système d'Information Hospitalier (SIH) est une solution complète de gestion hospitalière conçue pour les grands hôpitaux européens. Cette application web fullstack développée en Java/Spring Boot offre tous les modules nécessaires pour gérer efficacement un établissement de santé de haut niveau.

## ✨ Fonctionnalités Principales

### 👥 Gestion des Patients
- **Numéro unique du patient** - Chaque patient possède un identifiant unique généré automatiquement
- Enregistrement complet des informations personnelles et médicales
- Gestion des allergies, antécédents médicaux et médicaments actuels
- Suivi des contacts d'urgence et informations d'assurance
- Recherche avancée et filtrage des patients
- Historique complet des visites et consultations

### 👨‍⚕️ Gestion du Personnel Médical
- Profils complets des médecins avec spécialisations
- Gestion des horaires et disponibilités
- Suivi des qualifications et certifications
- Hiérarchie et organisation par département

### 🏥 Gestion des Départements
- Organisation structurée par départements spécialisés
- Gestion des capacités et ressources
- Suivi des budgets par département
- Attribution du personnel et équipements

### 📅 Gestion des Rendez-vous
- Planification intelligente des consultations
- Notifications automatiques et rappels
- Gestion des urgences et priorités
- Reprogrammation et annulations

### 🛏️ Gestion des Admissions et Lits
- Suivi en temps réel de l'occupation des lits
- Gestion des admissions et sorties
- Attribution automatique des chambres
- Suivi des transferts entre services

### 📋 Dossier Médical Électronique
- Dossiers médicaux complets et sécurisés
- Historique détaillé des consultations
- Intégration des résultats de laboratoire et imagerie
- Gestion des prescriptions électroniques
- Signature numérique des documents

### 🏥 Gestion des Blocs Opératoires
- Planification des interventions chirurgicales
- Gestion des équipes chirurgicales
- Suivi pré et post-opératoire
- Rapports d'intervention détaillés

### 🧪 Laboratoire et Imagerie
- Gestion complète des analyses de laboratoire
- Suivi des résultats d'imagerie médicale
- Intégration avec les équipements médicaux
- Validation et signature des résultats

### 💊 Pharmacie
- Gestion du stock de médicaments
- Prescriptions électroniques
- Suivi des interactions médicamenteuses
- Gestion des stupéfiants et substances contrôlées

### 💰 Facturation et Paiements
- Facturation automatisée par service
- Gestion des assurances et remboursements
- Suivi des paiements et créances
- Rapports financiers détaillés

### 📊 Tableaux de Bord et Statistiques
- Indicateurs de performance en temps réel
- Statistiques par département et service
- Rapports d'activité personnalisables
- Analyses prédictives

## 🛠️ Technologies Utilisées

### Backend
- **Java 17** - Langage de programmation principal
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistence des données
- **Spring Security** - Sécurité et authentification
- **Spring Web** - APIs REST
- **PostgreSQL** - Base de données principale
- **H2** - Base de données pour les tests

### Frontend
- **HTML5** - Structure des pages
- **CSS3** - Styles et mise en forme
- **JavaScript** - Interactivité côté client
- **Bootstrap 5** - Framework CSS responsive
- **Chart.js** - Graphiques et visualisations

### Outils et Bibliothèques
- **Swagger/OpenAPI** - Documentation API
- **Lombok** - Réduction du code boilerplate
- **MapStruct** - Mapping entre objets
- **JWT** - Authentification stateless
- **iText PDF** - Génération de documents PDF
- **Apache POI** - Manipulation de fichiers Excel

## 🚀 Installation et Configuration

### Prérequis
- Java 17 ou supérieur
- Maven 3.8+
- PostgreSQL 12+
- Git

### 1. Cloner le repository
```bash
git clone <repository-url>
cd hospital-management-system
```

### 2. Configuration de la base de données
```sql
-- Créer la base de données PostgreSQL
CREATE DATABASE hospital_db;
CREATE USER hospital_user WITH PASSWORD 'hospital_pass';
GRANT ALL PRIVILEGES ON DATABASE hospital_db TO hospital_user;
```

### 3. Configuration de l'application
Modifier le fichier `src/main/resources/application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hospital_db
    username: hospital_user
    password: hospital_pass
```

### 4. Installation des dépendances
```bash
mvn clean install
```

### 5. Lancement de l'application
```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse : http://localhost:8080

## 📚 Documentation API

La documentation API complète est disponible via Swagger UI :
- **URL** : http://localhost:8080/api/swagger-ui/index.html
- **Spécification OpenAPI** : http://localhost:8080/api/v3/api-docs

## 🔐 Sécurité

### Authentification
- Authentification JWT pour les APIs
- Gestion des rôles et permissions
- Chiffrement des mots de passe avec BCrypt

### Protection des Données
- Chiffrement des données sensibles
- Audit trail complet
- Contrôle d'accès basé sur les rôles (RBAC)
- Conformité RGPD

## 📱 Interface Utilisateur

### Tableau de Bord Principal
- Vue d'ensemble des activités de l'hôpital
- Statistiques en temps réel
- Actions rapides pour les tâches communes
- Notifications et alertes

### Modules Principaux
1. **Gestion des Patients** - Interface complète pour l'enregistrement et le suivi
2. **Planning Médical** - Calendrier des consultations et interventions
3. **Dossiers Médicaux** - Accès sécurisé aux informations médicales
4. **Facturation** - Gestion financière et administrative
5. **Rapports** - Génération de rapports personnalisés

## 🏗️ Architecture

### Structure du Projet
```
src/
├── main/
│   ├── java/com/hospital/
│   │   ├── entity/         # Entités JPA
│   │   ├── repository/     # Repositories Spring Data
│   │   ├── service/        # Services métier
│   │   ├── controller/     # Contrôleurs REST
│   │   ├── config/         # Configuration Spring
│   │   └── dto/            # Data Transfer Objects
│   └── resources/
│       ├── static/         # Ressources statiques (CSS, JS, images)
│       ├── templates/      # Templates Thymeleaf
│       └── application.yml # Configuration application
└── test/                   # Tests unitaires et d'intégration
```

### Base de Données
Le système utilise une architecture de base de données relationnelle avec les tables principales :
- `patients` - Informations des patients
- `doctors` - Profils des médecins
- `departments` - Départements hospitaliers
- `appointments` - Rendez-vous
- `admissions` - Admissions hospitalières
- `medical_records` - Dossiers médicaux
- `prescriptions` - Prescriptions médicales
- `bills` - Facturation
- `lab_results` - Résultats de laboratoire
- `imaging_results` - Résultats d'imagerie

## 🧪 Tests

### Exécution des tests
```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn integration-test

# Couverture de code
mvn jacoco:report
```

## 📈 Monitoring et Performance

### Métriques Spring Boot Actuator
- Health checks : http://localhost:8080/api/actuator/health
- Métriques : http://localhost:8080/api/actuator/metrics
- Info application : http://localhost:8080/api/actuator/info

### Optimisations
- Mise en cache avec Spring Cache
- Pagination pour les grandes listes
- Lazy loading pour les relations JPA
- Indexation des champs de recherche fréquents

## 🔧 Configuration Avancée

### Variables d'Environnement
```bash
export DB_USERNAME=hospital_user
export DB_PASSWORD=hospital_pass
export JWT_SECRET=your-secret-key
export MAIL_HOST=smtp.hospital.com
export UPLOAD_PATH=/opt/hospital/uploads
```

### Profils Spring
- `dev` - Développement avec H2 en mémoire
- `test` - Tests automatisés
- `prod` - Production avec PostgreSQL

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou support technique :
- Email : support@hospital-system.com
- Documentation : [Wiki du projet]
- Issues : [GitHub Issues]

## 🗺️ Roadmap

### Version 2.0 (À venir)
- [ ] Module de télémédecine
- [ ] Intégration HL7 FHIR
- [ ] Application mobile
- [ ] Intelligence artificielle pour le diagnostic
- [ ] Intégration IoT pour les dispositifs médicaux

### Version 1.1 (En cours)
- [x] API REST complète pour les patients
- [x] Interface web responsive
- [x] Documentation Swagger
- [ ] Module de gestion du personnel complet
- [ ] Système de notifications en temps réel
- [ ] Rapports avancés avec export PDF/Excel

---

**Développé avec ❤️ pour améliorer les soins de santé**