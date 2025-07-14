# 🏥 Tutoriel d'Installation Complet - Système d'Information Hospitalier (SIH)

## 📋 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Prérequis généraux](#prérequis-généraux)
3. [Installation sur Ubuntu Desktop (localhost)](#installation-sur-ubuntu-desktop-localhost)
4. [Installation sur Ubuntu Server LTS](#installation-sur-ubuntu-server-lts)
5. [Installation sur CentOS](#installation-sur-centos)
6. [Configuration post-installation](#configuration-post-installation)
7. [Tests et vérification](#tests-et-vérification)
8. [Maintenance et dépannage](#maintenance-et-dépannage)
9. [FAQ](#faq)

---

## 🎯 Vue d'ensemble

Le Système d'Information Hospitalier (SIH) est une solution complète pour la gestion hospitalière incluant :

- **Gestion des patients** et dossiers médicaux
- **Laboratoire (LIMS)** - Système de gestion d'informations de laboratoire
- **Imagerie (RIS)** - Système d'information radiologique
- **Pharmacie intégrée** avec gestion des stocks
- **Facturation et assurances**
- **File d'attente intelligente**
- **Gestion des vaccins** et carnets de vaccination
- **Inventaire hospitalier** complet
- **Rapports et statistiques** avancés

### 🏗️ Architecture technique

- **Backend** : Spring Boot 3.2.0 + Java 17
- **Base de données** : PostgreSQL 15+ (production) / H2 (développement)
- **Frontend** : HTML5, CSS3, JavaScript moderne, Bootstrap 5
- **API** : REST avec documentation Swagger/OpenAPI
- **Sécurité** : JWT, Spring Security
- **Formats** : JSON, PDF, Excel

---

## 🔧 Prérequis généraux

### Matériel recommandé

| Configuration | Minimum | Recommandé | Production |
|---------------|---------|------------|------------|
| **CPU** | 2 cores | 4 cores | 8+ cores |
| **RAM** | 4 GB | 8 GB | 16+ GB |
| **Stockage** | 20 GB | 50 GB | 200+ GB SSD |
| **Réseau** | 100 Mbps | 1 Gbps | 1+ Gbps |

### Logiciels requis

- **Java 17** (OpenJDK ou Oracle JDK)
- **PostgreSQL 15+** (production)
- **Maven 3.8+**
- **Git**
- **Nginx** (optionnel, pour proxy inverse)

---

## 🖥️ Installation sur Ubuntu Desktop (localhost)

### Étape 1 : Préparation du système

```bash
# Mise à jour du système
sudo apt update && sudo apt upgrade -y

# Installation des outils de base
sudo apt install -y curl wget apt-transport-https ca-certificates gnupg lsb-release
```

### Étape 2 : Installation de Java 17

```bash
# Installation d'OpenJDK 17
sudo apt install -y openjdk-17-jdk

# Vérification de l'installation
java -version
javac -version

# Configuration de JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
```

### Étape 3 : Installation de PostgreSQL

```bash
# Installation de PostgreSQL
sudo apt install -y postgresql postgresql-contrib

# Démarrage et activation du service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Configuration de l'utilisateur PostgreSQL
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'hospital123';"

# Création de la base de données
sudo -u postgres createdb hospital_db
```

### Étape 4 : Installation de Maven

```bash
# Installation de Maven
sudo apt install -y maven

# Vérification
mvn -version
```

### Étape 5 : Installation de Git

```bash
# Installation de Git
sudo apt install -y git

# Configuration (remplacez par vos informations)
git config --global user.name "Votre Nom"
git config --global user.email "votre.email@domain.com"
```

### Étape 6 : Téléchargement et installation du SIH

```bash
# Création du répertoire de travail
mkdir -p ~/hospital-system
cd ~/hospital-system

# Clonage du projet (ou extraction de l'archive)
# Si vous avez le projet en archive :
# tar -xzf hospital-management-system.tar.gz
# cd hospital-management-system

# Installation des dépendances
mvn clean install -DskipTests

# Configuration de la base de données
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

### Étape 7 : Configuration

Éditez le fichier `src/main/resources/application.yml` :

```yaml
server:
  port: 8080
  
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hospital_db
    username: postgres
    password: hospital123
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

logging:
  level:
    com.hospital: INFO
    org.springframework.security: DEBUG
```

### Étape 8 : Démarrage de l'application

```bash
# Démarrage de l'application
mvn spring-boot:run

# Ou compilation et exécution du JAR
mvn clean package -DskipTests
java -jar target/hospital-management-system-1.0.0.jar
```

### Étape 9 : Vérification

Ouvrez votre navigateur et accédez à :

- **Application principale** : http://localhost:8080
- **Dashboard admin** : http://localhost:8080/admin-dashboard.html
- **Documentation API** : http://localhost:8080/swagger-ui/index.html

---

## 🖥️ Installation sur Ubuntu Server LTS

### Étape 1 : Préparation du serveur

```bash
# Connexion au serveur
ssh user@your-server-ip

# Mise à jour du système
sudo apt update && sudo apt upgrade -y

# Installation des outils essentiels
sudo apt install -y curl wget git unzip vim htop ufw
```

### Étape 2 : Configuration du firewall

```bash
# Configuration du firewall
sudo ufw allow ssh
sudo ufw allow 8080
sudo ufw allow 80
sudo ufw allow 443
sudo ufw --force enable
```

### Étape 3 : Installation de Java 17

```bash
# Installation d'OpenJDK 17
sudo apt install -y openjdk-17-jdk

# Configuration de JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' | sudo tee -a /etc/environment
echo 'export PATH=$PATH:$JAVA_HOME/bin' | sudo tee -a /etc/environment
source /etc/environment
```

### Étape 4 : Installation de PostgreSQL

```bash
# Installation de PostgreSQL 15
sudo apt install -y postgresql-15 postgresql-client-15 postgresql-contrib-15

# Configuration de PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Configuration de l'utilisateur
sudo -u postgres psql << EOF
ALTER USER postgres PASSWORD 'HospitalSecure2024!';
CREATE DATABASE hospital_db;
CREATE USER hospital_user WITH ENCRYPTED PASSWORD 'HospitalUser2024!';
GRANT ALL PRIVILEGES ON DATABASE hospital_db TO hospital_user;
ALTER DATABASE hospital_db OWNER TO hospital_user;
\q
EOF

# Configuration des connexions
sudo nano /etc/postgresql/15/main/postgresql.conf
# Modifiez : listen_addresses = '*'

sudo nano /etc/postgresql/15/main/pg_hba.conf
# Ajoutez : host all all 0.0.0.0/0 md5

sudo systemctl restart postgresql
```

### Étape 5 : Installation de Maven

```bash
# Installation de Maven
sudo apt install -y maven

# Vérification
mvn -version
```

### Étape 6 : Création de l'utilisateur système

```bash
# Création de l'utilisateur dédié
sudo useradd -m -s /bin/bash hospital
sudo usermod -aG sudo hospital

# Changement vers l'utilisateur hospital
sudo su - hospital
```

### Étape 7 : Déploiement de l'application

```bash
# Téléchargement du code source
cd /home/hospital
git clone <your-repository-url> hospital-system
cd hospital-system

# Ou copie depuis votre machine locale
# scp -r hospital-system/ hospital@your-server:/home/hospital/

# Installation des dépendances
mvn clean install -DskipTests
```

### Étape 8 : Configuration pour la production

Créez le fichier `src/main/resources/application-prod.yml` :

```yaml
server:
  port: 8080
  
spring:
  profiles:
    active: prod
    
  datasource:
    url: jdbc:postgresql://localhost:5432/hospital_db
    username: hospital_user
    password: HospitalUser2024!
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    
  mail:
    host: smtp.your-domain.com
    port: 587
    username: noreply@your-hospital.com
    password: ${MAIL_PASSWORD}
    
logging:
  level:
    com.hospital: INFO
    org.springframework.web: WARN
  file:
    name: /var/log/hospital/application.log
    
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

### Étape 9 : Création du service systemd

```bash
# Création du service
sudo nano /etc/systemd/system/hospital-system.service
```

Contenu du fichier :

```ini
[Unit]
Description=Hospital Management System
After=network.target postgresql.service

[Service]
Type=simple
User=hospital
Group=hospital
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /home/hospital/hospital-system/target/hospital-management-system-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=hospital-system
KillMode=mixed
KillSignal=SIGTERM

Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
```

### Étape 10 : Démarrage du service

```bash
# Rechargement de systemd
sudo systemctl daemon-reload

# Compilation finale
mvn clean package -DskipTests -Pprod

# Activation et démarrage du service
sudo systemctl enable hospital-system
sudo systemctl start hospital-system

# Vérification du statut
sudo systemctl status hospital-system
```

### Étape 11 : Configuration de Nginx (optionnel)

```bash
# Installation de Nginx
sudo apt install -y nginx

# Configuration du site
sudo nano /etc/nginx/sites-available/hospital-system
```

Contenu de la configuration Nginx :

```nginx
server {
    listen 80;
    server_name your-hospital-domain.com;

    # Redirection HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-hospital-domain.com;

    # Certificats SSL (à configurer avec Let's Encrypt)
    ssl_certificate /etc/ssl/certs/hospital.crt;
    ssl_certificate_key /etc/ssl/private/hospital.key;

    # Configuration SSL
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;

    # Headers de sécurité
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload";

    # Proxy vers l'application Spring Boot
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 30;
        proxy_send_timeout 30;
        proxy_read_timeout 30;
    }

    # Gestion des fichiers statiques
    location /static/ {
        root /home/hospital/hospital-system/src/main/resources;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Logs
    access_log /var/log/nginx/hospital_access.log;
    error_log /var/log/nginx/hospital_error.log;
}
```

Activation de la configuration :

```bash
# Activation du site
sudo ln -s /etc/nginx/sites-available/hospital-system /etc/nginx/sites-enabled/

# Test de la configuration
sudo nginx -t

# Redémarrage de Nginx
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

## 🔴 Installation sur CentOS

### Étape 1 : Préparation du système

```bash
# Mise à jour du système
sudo yum update -y

# Installation des outils de base
sudo yum install -y curl wget git unzip vim htop

# Installation d'EPEL repository
sudo yum install -y epel-release
```

### Étape 2 : Configuration du firewall

```bash
# Configuration de firewalld
sudo systemctl start firewalld
sudo systemctl enable firewalld

# Ouverture des ports
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --reload
```

### Étape 3 : Installation de Java 17

```bash
# Installation d'OpenJDK 17
sudo yum install -y java-17-openjdk java-17-openjdk-devel

# Configuration de JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' | sudo tee -a /etc/environment
echo 'export PATH=$PATH:$JAVA_HOME/bin' | sudo tee -a /etc/environment
source /etc/environment

# Définition comme version par défaut
sudo alternatives --config java
```

### Étape 4 : Installation de PostgreSQL

```bash
# Installation du repository PostgreSQL
sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm

# Installation de PostgreSQL 15
sudo yum install -y postgresql15-server postgresql15-contrib

# Initialisation de la base de données
sudo /usr/pgsql-15/bin/postgresql-15-setup initdb

# Démarrage et activation du service
sudo systemctl start postgresql-15
sudo systemctl enable postgresql-15

# Configuration de l'utilisateur
sudo -u postgres /usr/pgsql-15/bin/psql << EOF
ALTER USER postgres PASSWORD 'HospitalSecure2024!';
CREATE DATABASE hospital_db;
CREATE USER hospital_user WITH ENCRYPTED PASSWORD 'HospitalUser2024!';
GRANT ALL PRIVILEGES ON DATABASE hospital_db TO hospital_user;
ALTER DATABASE hospital_db OWNER TO hospital_user;
\q
EOF
```

### Étape 5 : Installation de Maven

```bash
# Téléchargement et installation de Maven
cd /opt
sudo wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
sudo tar xzf apache-maven-3.9.5-bin.tar.gz
sudo ln -s apache-maven-3.9.5 maven

# Configuration des variables d'environnement
echo 'export M2_HOME=/opt/maven' | sudo tee -a /etc/environment
echo 'export PATH=$PATH:$M2_HOME/bin' | sudo tee -a /etc/environment
source /etc/environment
```

### Étape 6 : Création de l'utilisateur système

```bash
# Création de l'utilisateur
sudo useradd -m -s /bin/bash hospital
sudo usermod -aG wheel hospital

# Changement vers l'utilisateur hospital
sudo su - hospital
```

### Étape 7 : Déploiement de l'application

```bash
# Téléchargement du code source
cd /home/hospital
git clone <your-repository-url> hospital-system
cd hospital-system

# Installation des dépendances
/opt/maven/bin/mvn clean install -DskipTests
```

### Étape 8 : Configuration pour CentOS

Utilisez la même configuration que pour Ubuntu Server, mais adaptez les chemins :

```yaml
# Dans application-prod.yml
logging:
  file:
    name: /var/log/hospital/application.log
```

### Étape 9 : Création du service systemd

```bash
# Création du répertoire de logs
sudo mkdir -p /var/log/hospital
sudo chown hospital:hospital /var/log/hospital

# Création du service (même contenu que Ubuntu)
sudo nano /etc/systemd/system/hospital-system.service
```

### Étape 10 : Configuration SELinux

```bash
# Configuration de SELinux pour l'application
sudo setsebool -P httpd_can_network_connect 1
sudo semanage port -a -t http_port_t -p tcp 8080

# Ou désactivation temporaire de SELinux (non recommandé en production)
# sudo setenforce 0
```

### Étape 11 : Démarrage du service

```bash
# Compilation et démarrage
/opt/maven/bin/mvn clean package -DskipTests

sudo systemctl daemon-reload
sudo systemctl enable hospital-system
sudo systemctl start hospital-system
sudo systemctl status hospital-system
```

---

## ⚙️ Configuration post-installation

### Configuration des emails

```bash
# Configuration SMTP
nano src/main/resources/application.yml

# Ajoutez vos paramètres SMTP
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-hospital@gmail.com
    password: your-app-password
```

### Configuration des sauvegardes

Créez un script de sauvegarde automatique :

```bash
# Création du script de sauvegarde
sudo nano /usr/local/bin/hospital-backup.sh
```

Contenu du script :

```bash
#!/bin/bash

BACKUP_DIR="/backup/hospital"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="hospital_db"
DB_USER="hospital_user"

# Création du répertoire de sauvegarde
mkdir -p $BACKUP_DIR

# Sauvegarde de la base de données
pg_dump -h localhost -U $DB_USER -d $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# Sauvegarde des fichiers de l'application
tar -czf $BACKUP_DIR/app_backup_$DATE.tar.gz /home/hospital/hospital-system

# Nettoyage des anciennes sauvegardes (garde 30 jours)
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "Sauvegarde terminée : $DATE"
```

```bash
# Rendre le script exécutable
sudo chmod +x /usr/local/bin/hospital-backup.sh

# Programmer l'exécution quotidienne
sudo crontab -e
# Ajoutez : 0 2 * * * /usr/local/bin/hospital-backup.sh
```

### Configuration des certificats SSL

```bash
# Installation de Certbot pour Let's Encrypt
sudo apt install -y certbot python3-certbot-nginx  # Ubuntu
sudo yum install -y certbot python3-certbot-nginx  # CentOS

# Obtention du certificat
sudo certbot --nginx -d your-hospital-domain.com

# Auto-renouvellement
sudo crontab -e
# Ajoutez : 0 12 * * * /usr/bin/certbot renew --quiet
```

---

## 🧪 Tests et vérification

### Tests de connectivité

```bash
# Test de l'application
curl -I http://localhost:8080

# Test de l'API
curl -X GET http://localhost:8080/api/dashboard/statistics

# Test de la base de données
sudo -u postgres psql -d hospital_db -c "SELECT count(*) FROM information_schema.tables;"
```

### Tests fonctionnels

1. **Accès à l'interface** : http://your-server:8080
2. **Dashboard administrateur** : http://your-server:8080/admin-dashboard.html
3. **API Documentation** : http://your-server:8080/swagger-ui/index.html

### Tests de performance

```bash
# Installation d'Apache Bench
sudo apt install -y apache2-utils  # Ubuntu
sudo yum install -y httpd-tools    # CentOS

# Test de charge simple
ab -n 1000 -c 10 http://localhost:8080/
```

### Monitoring des logs

```bash
# Logs de l'application
sudo journalctl -u hospital-system -f

# Logs de PostgreSQL
sudo tail -f /var/log/postgresql/postgresql-15-main.log  # Ubuntu
sudo tail -f /var/lib/pgsql/15/data/log/postgresql-*.log  # CentOS

# Logs Nginx
sudo tail -f /var/log/nginx/hospital_access.log
```

---

## 🔧 Maintenance et dépannage

### Commandes utiles

```bash
# Redémarrage de l'application
sudo systemctl restart hospital-system

# Vérification du statut
sudo systemctl status hospital-system

# Logs en temps réel
sudo journalctl -u hospital-system -f

# Vérification de l'utilisation des ressources
htop
```

### Problèmes courants

#### Erreur de connexion à la base de données

```bash
# Vérification du service PostgreSQL
sudo systemctl status postgresql

# Test de connexion
psql -h localhost -U hospital_user -d hospital_db

# Redémarrage de PostgreSQL
sudo systemctl restart postgresql
```

#### Problème de mémoire

```bash
# Ajustement de la mémoire JVM
sudo nano /etc/systemd/system/hospital-system.service

# Modifiez ExecStart pour ajouter :
ExecStart=/usr/bin/java -Xmx2g -Xms1g -jar ...

sudo systemctl daemon-reload
sudo systemctl restart hospital-system
```

#### Problème de certificats SSL

```bash
# Renouvellement du certificat
sudo certbot renew

# Vérification de la validité
sudo certbot certificates
```

### Mise à jour de l'application

```bash
# Sauvegarde avant mise à jour
/usr/local/bin/hospital-backup.sh

# Arrêt de l'application
sudo systemctl stop hospital-system

# Mise à jour du code
cd /home/hospital/hospital-system
git pull origin main

# Recompilation
mvn clean package -DskipTests

# Redémarrage
sudo systemctl start hospital-system
```

---

## ❓ FAQ

### Q : Puis-je utiliser MySQL au lieu de PostgreSQL ?

R : Oui, modifiez simplement la configuration dans `application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_db
    username: hospital_user
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
```

### Q : Comment changer le port par défaut ?

R : Modifiez dans `application.yml` :

```yaml
server:
  port: 9090
```

### Q : Comment activer HTTPS directement dans Spring Boot ?

R : Ajoutez dans `application.yml` :

```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: password
    key-store-type: PKCS12
```

### Q : Comment configurer un cluster de base de données ?

R : Utilisez des URLs de connexion multiples :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db1:5432,db2:5432/hospital_db
    username: hospital_user
    password: password
```

### Q : Comment monitorer l'application en production ?

R : Utilisez Spring Boot Actuator avec Prometheus et Grafana :

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  metrics:
    export:
      prometheus:
        enabled: true
```

---

## 📞 Support

Pour toute question ou problème :

1. **Documentation** : Consultez le README.md du projet
2. **Logs** : Vérifiez les logs avec `journalctl -u hospital-system`
3. **API** : Utilisez Swagger UI pour tester les endpoints
4. **Issues** : Créez une issue sur le repository Git

---

**© 2024 Système d'Information Hospitalier - Version 1.0.0**