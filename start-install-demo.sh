#!/bin/bash

# =============================================================================
# Script de Démarrage pour Démo de l'Interface d'Installation
# Système d'Information Hospitalier
# =============================================================================

set -e  # Arrêter en cas d'erreur

# Configuration
JAVA_VERSION_REQUIRED="17"
MAVEN_VERSION_REQUIRED="3.6"
PORT="8080"
LOG_FILE="logs/install-demo.log"

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour l'affichage avec couleurs
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}"
    echo "=============================================="
    echo "   🏥 SYSTÈME HOSPITALIER - DÉMO INSTALL"
    echo "=============================================="
    echo -e "${NC}"
}

# Fonction pour vérifier Java
check_java() {
    print_info "Vérification de Java..."
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "${JAVA_VERSION}" -ge "${JAVA_VERSION_REQUIRED}" ]; then
            print_success "Java ${JAVA_VERSION} détecté (requis: ${JAVA_VERSION_REQUIRED}+)"
            return 0
        else
            print_error "Java ${JAVA_VERSION} détecté, mais Java ${JAVA_VERSION_REQUIRED}+ est requis"
            return 1
        fi
    else
        print_error "Java non trouvé. Veuillez installer Java ${JAVA_VERSION_REQUIRED}+"
        return 1
    fi
}

# Fonction pour vérifier Maven
check_maven() {
    print_info "Vérification de Maven..."
    
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn -version 2>&1 | head -1 | awk '{print $3}')
        print_success "Maven ${MAVEN_VERSION} détecté"
        return 0
    else
        print_error "Maven non trouvé. Veuillez installer Maven ${MAVEN_VERSION_REQUIRED}+"
        return 1
    fi
}

# Fonction pour vérifier le port
check_port() {
    print_info "Vérification du port ${PORT}..."
    
    if lsof -Pi :${PORT} -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Le port ${PORT} est déjà utilisé"
        print_info "Tentative d'arrêt du processus existant..."
        
        PID=$(lsof -ti:${PORT})
        if [ ! -z "$PID" ]; then
            kill -9 $PID 2>/dev/null || true
            sleep 2
            
            if lsof -Pi :${PORT} -sTCP:LISTEN -t >/dev/null 2>&1; then
                print_error "Impossible de libérer le port ${PORT}"
                return 1
            else
                print_success "Port ${PORT} libéré"
            fi
        fi
    else
        print_success "Port ${PORT} disponible"
    fi
    return 0
}

# Fonction pour créer les répertoires nécessaires
create_directories() {
    print_info "Création des répertoires nécessaires..."
    
    mkdir -p logs
    mkdir -p target
    mkdir -p src/main/resources/static
    mkdir -p src/main/resources/templates
    
    print_success "Répertoires créés"
}

# Fonction pour nettoyer l'installation précédente
clean_previous_install() {
    print_info "Nettoyage de l'installation précédente..."
    
    # Supprimer le fichier de verrouillage d'installation
    if [ -f "install.lock" ]; then
        rm -f install.lock
        print_success "Fichier de verrouillage supprimé"
    fi
    
    # Supprimer les fichiers de configuration générés
    if [ -f "application-production.yml" ]; then
        rm -f application-production.yml
        print_info "Fichier de configuration précédent supprimé"
    fi
    
    print_success "Nettoyage terminé"
}

# Fonction pour compiler le projet
compile_project() {
    print_info "Compilation du projet..."
    
    # Compilation sans les tests pour plus de rapidité
    if mvn clean compile -DskipTests -q > "${LOG_FILE}" 2>&1; then
        print_success "Compilation réussie"
        return 0
    else
        print_error "Échec de la compilation. Voir ${LOG_FILE} pour les détails"
        return 1
    fi
}

# Fonction pour démarrer l'application
start_application() {
    print_info "Démarrage de l'application..."
    print_info "URL d'accès: http://localhost:${PORT}"
    print_info "Interface d'installation: http://localhost:${PORT}/install"
    print_info "Appuyez sur Ctrl+C pour arrêter"
    
    echo ""
    print_warning "L'application va démarrer. Patientez quelques instants..."
    echo ""
    
    # Démarrer Spring Boot en mode développement
    mvn spring-boot:run -Dspring-boot.run.profiles=dev \
                       -Dspring-boot.run.jvmArguments="-Xmx1g -Xms512m" \
                       -Dserver.port=${PORT} 2>&1 | tee -a "${LOG_FILE}"
}

# Fonction pour afficher les instructions post-démarrage
show_instructions() {
    echo ""
    print_header
    print_success "Application démarrée avec succès!"
    echo ""
    echo -e "${YELLOW}📋 INSTRUCTIONS:${NC}"
    echo ""
    echo "1. 🌐 Ouvrez votre navigateur"
    echo "2. 🔗 Accédez à: http://localhost:${PORT}"
    echo "3. 🚀 Suivez l'assistant d'installation en 5 étapes"
    echo "4. ⚙️  Configurez votre base de données (H2 recommandé pour le test)"
    echo "5. 👤 Créez votre compte administrateur"
    echo "6. ✅ Terminez l'installation"
    echo ""
    print_info "Pour les tests, utilisez H2 (base intégrée) - aucune configuration requise"
    print_info "Logs disponibles dans: ${LOG_FILE}"
    echo ""
}

# Fonction de nettoyage à l'arrêt
cleanup() {
    echo ""
    print_info "Arrêt de l'application..."
    print_success "Merci d'avoir testé l'interface d'installation!"
}

# Piège pour capturer Ctrl+C
trap cleanup EXIT

# ====================
# SCRIPT PRINCIPAL
# ====================

main() {
    print_header
    
    print_info "Vérification des prérequis..."
    
    # Vérifications
    check_java || exit 1
    check_maven || exit 1
    check_port || exit 1
    
    # Préparation
    create_directories
    clean_previous_install
    
    # Compilation
    compile_project || exit 1
    
    # Affichage des instructions
    show_instructions
    
    # Démarrage
    start_application
}

# Exécution du script principal
main "$@"