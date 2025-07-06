#!/bin/bash

echo "🏥 SYSTÈME D'INFORMATION HOSPITALIER - DÉMARRAGE"
echo "=================================================="

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

# Fonction pour choisir le profil
choose_profile() {
    echo ""
    echo -e "${YELLOW}🔧 Choisissez le profil de démarrage:${NC}"
    echo "1) Development (H2 - Base de données en mémoire) - RECOMMANDÉ"
    echo "2) Production (PostgreSQL)"
    echo ""
    read -p "Votre choix (1 ou 2): " choice
    
    case $choice in
        1)
            PROFILE="dev"
            echo -e "${GREEN}✅ Profil Development sélectionné (H2)${NC}"
            ;;
        2)
            PROFILE="prod"
            echo -e "${GREEN}✅ Profil Production sélectionné (PostgreSQL)${NC}"
            ;;
        *)
            echo -e "${YELLOW}⚠️  Choix non valide, utilisation du profil Development par défaut${NC}"
            PROFILE="dev"
            ;;
    esac
}

# Nettoyer et compiler
echo ""
echo -e "${BLUE}🧹 Nettoyage et compilation...${NC}"
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ ERREUR: Échec de la compilation${NC}"
    echo -e "${YELLOW}💡 Vérifiez les erreurs ci-dessus et corrigez-les${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilation réussie${NC}"

# Construire le JAR si nécessaire
if [ ! -f "target/hospital-management-system-1.0.0.jar" ]; then
    echo -e "${BLUE}📦 Construction du JAR...${NC}"
    mvn package -DskipTests -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ ERREUR: Échec de la construction du JAR${NC}"
        exit 1
    fi
fi

# Choisir le profil
choose_profile

# Créer les répertoires nécessaires
mkdir -p uploads reports logs

echo ""
echo -e "${BLUE}🚀 Démarrage de l'application...${NC}"
echo -e "${YELLOW}📋 Informations importantes:${NC}"

if [ "$PROFILE" = "dev" ]; then
    echo -e "${GREEN}🌐 Application: http://localhost:8080${NC}"
    echo -e "${GREEN}🗄️  Console H2: http://localhost:8080/h2-console${NC}"
    echo -e "${GREEN}📚 API Documentation: http://localhost:8080/swagger-ui/index.html${NC}"
    echo -e "${YELLOW}🔑 Console H2 - JDBC URL: jdbc:h2:mem:hospital_db${NC}"
    echo -e "${YELLOW}🔑 Console H2 - Username: sa (pas de mot de passe)${NC}"
else
    echo -e "${GREEN}🌐 Application: http://localhost:8080/api${NC}"
    echo -e "${GREEN}📚 API Documentation: http://localhost:8080/api/swagger-ui/index.html${NC}"
fi

echo -e "${YELLOW}👤 Compte Admin: admin / Admin123!${NC}"
echo ""
echo -e "${BLUE}📋 Pour arrêter l'application: Ctrl+C${NC}"
echo ""

# Démarrer l'application
if [ "$PROFILE" = "dev" ]; then
    java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev
else
    java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=prod
fi