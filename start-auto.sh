#!/bin/bash

echo "🏥 SYSTÈME D'INFORMATION HOSPITALIER - DÉMARRAGE AUTOMATIQUE"
echo "============================================================="

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERREUR: Vous devez exécuter ce script depuis le répertoire racine du projet${NC}"
    echo -e "${YELLOW}💡 Exécutez: cd /workspace${NC}"
    exit 1
fi

echo -e "${BLUE}📍 Répertoire actuel: $(pwd)${NC}"

# Profil automatiquement sélectionné : Development
PROFILE="dev"
echo -e "${GREEN}✅ Profil Development sélectionné automatiquement (H2)${NC}"

# Nettoyer et compiler
echo ""
echo -e "${BLUE}🧹 Nettoyage et compilation...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERREUR: Échec de la compilation${NC}"
    echo -e "${YELLOW}💡 Essayez: mvn clean compile (sans -q pour voir les détails)${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilation réussie${NC}"

# Construire le JAR si nécessaire
if [ ! -f "target/hospital-management-system-1.0.0.jar" ]; then
    echo -e "${BLUE}📦 Construction du JAR...${NC}"
    mvn package -DskipTests -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ ERREUR: Échec de la construction du JAR${NC}"
        echo -e "${YELLOW}💡 Essayez: mvn package -DskipTests (sans -q pour voir les détails)${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ JAR construit avec succès${NC}"
else
    echo -e "${GREEN}✅ JAR déjà disponible${NC}"
fi

# Créer les répertoires nécessaires
mkdir -p uploads reports logs

echo ""
echo -e "${BLUE}🚀 Démarrage de l'application en mode Development...${NC}"
echo -e "${YELLOW}📋 Informations importantes:${NC}"
echo -e "${GREEN}🌐 Application: http://localhost:8080${NC}"
echo -e "${GREEN}🗄️  Console H2: http://localhost:8080/h2-console${NC}"
echo -e "${GREEN}📚 API Documentation: http://localhost:8080/swagger-ui/index.html${NC}"
echo -e "${GREEN}🏠 Dashboard Admin: http://localhost:8080/admin-dashboard.html${NC}"
echo ""
echo -e "${YELLOW}🔑 Console H2 - JDBC URL: jdbc:h2:mem:hospital_db${NC}"
echo -e "${YELLOW}🔑 Console H2 - Username: sa (pas de mot de passe)${NC}"
echo -e "${YELLOW}👤 Compte Admin: admin / Admin123!${NC}"
echo ""
echo -e "${BLUE}📋 Pour arrêter l'application: Ctrl+C${NC}"
echo ""
echo -e "${GREEN}🎯 Démarrage en cours...${NC}"

# Démarrer l'application
java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev