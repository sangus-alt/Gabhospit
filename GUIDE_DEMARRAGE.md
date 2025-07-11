# 🏥 GUIDE DE DÉMARRAGE - SYSTÈME HOSPITALIER

## 📍 **RÉPERTOIRE D'EXÉCUTION OBLIGATOIRE**

⚠️ **IMPORTANT:** Toutes les commandes doivent être exécutées depuis le répertoire racine :

```bash
cd /workspace
```

## 🚀 **MÉTHODES DE DÉMARRAGE**

### **🎯 MÉTHODE RECOMMANDÉE : Script Automatique**

```bash
# 1. Aller dans le répertoire du projet
cd /workspace

# 2. Exécuter le script de démarrage
./start.sh
```

Le script vous proposera :
- **Option 1** : Development (H2) - **RECOMMANDÉ** ✅
- **Option 2** : Production (PostgreSQL)

### **🔧 MÉTHODE MANUELLE**

#### **A. Avec H2 (Base de données en mémoire) - FACILE**

```bash
# 1. Aller dans le répertoire
cd /workspace

# 2. Nettoyer et compiler
mvn clean compile

# 3. Construire le JAR
mvn package -DskipTests

# 4. Démarrer avec le profil development
java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev
```

#### **B. Avec PostgreSQL - AVANCÉ**

```bash
# 1. Vérifier que PostgreSQL est démarré
sudo service postgresql start

# 2. Aller dans le répertoire
cd /workspace

# 3. Compiler et démarrer
mvn clean compile
mvn package -DskipTests
java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=prod
```

## 🌐 **ACCÈS À L'APPLICATION**

### **Mode Development (H2)**
- **Application principale :** http://localhost:8080
- **Console H2 :** http://localhost:8080/h2-console
- **Documentation API :** http://localhost:8080/swagger-ui/index.html
- **Dashboard Admin :** http://localhost:8080/admin-dashboard.html

### **Mode Production (PostgreSQL)**  
- **Application principale :** http://localhost:8080/api
- **Documentation API :** http://localhost:8080/api/swagger-ui/index.html

## 🔑 **COMPTES PAR DÉFAUT**

### **Compte Administrateur**
- **Username :** `admin`
- **Password :** `Admin123!`

### **Console H2 (Mode Development)**
- **JDBC URL :** `jdbc:h2:mem:hospital_db`
- **Username :** `sa`
- **Password :** *(laissez vide)*

## 🗂️ **STRUCTURE DES FICHIERS DE CONFIGURATION**

```
src/main/resources/
├── application.yml          # Configuration PostgreSQL (production)
├── application-dev.yml      # Configuration H2 (development)
└── static/
    └── admin-dashboard.html  # Interface d'administration
```

## 🛠️ **RÉSOLUTION DES PROBLÈMES COURANTS**

### **❌ Erreur : "BUILD FAILURE - Cannot create resource output directory"**
```bash
# Solution 1 : Permissions
sudo chown -R ubuntu:ubuntu /workspace
cd /workspace

# Solution 2 : Nettoyer et recommencer
rm -rf target
mvn clean compile
```

### **❌ Erreur : "password authentication failed for user hospital_user"**
```bash
# Solution : Utiliser le mode development avec H2
java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev
```

### **❌ Erreur : "Could not find or load main class"**
```bash
# Solution : Reconstruire le JAR
cd /workspace
mvn clean package -DskipTests
```

### **❌ Port 8080 déjà utilisé**
```bash
# Solution 1 : Tuer le processus
sudo lsof -ti:8080 | xargs sudo kill -9

# Solution 2 : Utiliser un autre port
java -jar target/hospital-management-system-1.0.0.jar --server.port=8081
```

## 📋 **COMMANDES UTILES**

### **Compilation et Tests**
```bash
# Compilation simple
mvn clean compile

# Construction complète
mvn clean install

# Construction sans tests
mvn clean package -DskipTests

# Tests uniquement
mvn test
```

### **Démarrage avec Options**
```bash
# Mode development
java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev

# Mode production
java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=prod

# Port personnalisé
java -jar target/hospital-management-system-1.0.0.jar --server.port=8081

# Debug activé
java -jar target/hospital-management-system-1.0.0.jar --logging.level.com.hospital=DEBUG
```

## 🔍 **VÉRIFICATIONS AVANT DÉMARRAGE**

### **1. Vérifier la Structure du Projet**
```bash
cd /workspace
ls -la
# Vous devez voir : pom.xml, src/, target/, start.sh
```

### **2. Vérifier Java**
```bash
java -version
# Version recommandée : Java 17+
```

### **3. Vérifier Maven**
```bash
mvn -version
# Version recommandée : Maven 3.6+
```

### **4. Vérifier la Compilation**
```bash
mvn clean compile
# Doit se terminer par "BUILD SUCCESS"
```

## 🎯 **DÉMARRAGE RAPIDE - 3 ÉTAPES**

```bash
# 1. Position
cd /workspace

# 2. Script automatique  
./start.sh

# 3. Choisir option 1 (Development)
# ✅ Application disponible sur http://localhost:8080
```

## 📞 **SUPPORT**

En cas de problème :
1. Vérifiez que vous êtes dans `/workspace`
2. Utilisez le mode Development (H2) pour éviter les problèmes PostgreSQL
3. Relancez `mvn clean compile` en cas de doute
4. Consultez les logs pour identifier l'erreur exacte

---

**🎉 Bonne utilisation du Système d'Information Hospitalier !**