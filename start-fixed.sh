#!/bin/bash

echo "🏥 SYSTÈME D'INFORMATION HOSPITALIER - DÉMARRAGE CORRIGÉ"
echo "========================================================="

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Définir le répertoire du projet
PROJECT_DIR="/workspace"
MVN_CMD="/usr/bin/mvn"
JAVA_CMD="/usr/bin/java"

echo -e "${BLUE}📍 Répertoire de travail: $(pwd)${NC}"
echo -e "${BLUE}📁 Répertoire du projet: ${PROJECT_DIR}${NC}"

# Vérifier que le répertoire du projet existe
if [ ! -d "$PROJECT_DIR" ]; then
    echo -e "${RED}❌ ERREUR: Le répertoire du projet ${PROJECT_DIR} n'existe pas${NC}"
    echo -e "${YELLOW}💡 Le projet doit être dans /workspace${NC}"
    exit 1
fi

# Aller dans le répertoire du projet
cd "$PROJECT_DIR" || exit 1

# Vérifier que pom.xml existe
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ ERREUR: Fichier pom.xml non trouvé dans ${PROJECT_DIR}${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Fichier pom.xml trouvé${NC}"

# Vérifier que Maven existe
if [ ! -f "$MVN_CMD" ]; then
    echo -e "${RED}❌ ERREUR: Maven non trouvé à ${MVN_CMD}${NC}"
    echo -e "${YELLOW}💡 Installation de Maven...${NC}"
    sudo apt update && sudo apt install -y maven
    MVN_CMD="mvn"
fi

# Vérifier que Java existe
if [ ! -f "$JAVA_CMD" ]; then
    JAVA_CMD="java"
fi

echo -e "${GREEN}✅ Maven trouvé: ${MVN_CMD}${NC}"
echo -e "${GREEN}✅ Java trouvé: ${JAVA_CMD}${NC}"

# Profil automatiquement sélectionné : Development
PROFILE="dev"
echo -e "${GREEN}✅ Profil Development sélectionné automatiquement (H2)${NC}"

# Nettoyer et compiler
echo ""
echo -e "${BLUE}🧹 Nettoyage et compilation...${NC}"
$MVN_CMD clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERREUR: Échec de la compilation${NC}"
    echo -e "${YELLOW}💡 Essayez: $MVN_CMD clean compile (sans -q pour voir les détails)${NC}"
    echo -e "${YELLOW}💡 Exécution avec détails...${NC}"
    $MVN_CMD clean compile
    exit 1
fi

echo -e "${GREEN}✅ Compilation réussie${NC}"

# Construire le JAR si nécessaire
JAR_FILE="${PROJECT_DIR}/target/hospital-management-system-1.0.0.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${BLUE}📦 Construction du JAR...${NC}"
    $MVN_CMD package -DskipTests -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ ERREUR: Échec de la construction du JAR${NC}"
        echo -e "${YELLOW}💡 Essayez: $MVN_CMD package -DskipTests (sans -q pour voir les détails)${NC}"
        $MVN_CMD package -DskipTests
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
echo -e "${GREEN}🎯 Démarrage en cours...${NC}"
echo ""

# Démarrer l'application
$JAVA_CMD -jar "$JAR_FILE" --spring.profiles.active=dev