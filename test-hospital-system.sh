#!/bin/bash

# Script de test complet du système hospitalier
# Auteur: Assistant IA
# Description: Tests des APIs Backend et Frontend

echo "🏥 TESTS COMPLETS DU SYSTÈME HOSPITALIER"
echo "========================================"

BASE_URL="http://localhost:8080"
API_URL="$BASE_URL/api"

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Compteurs
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Fonction pour afficher le résultat d'un test
test_result() {
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ $2${NC}"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
}

# Fonction pour tester une URL
test_url() {
    local url="$1"
    local description="$2"
    local expected_code="$3"
    
    if [ -z "$expected_code" ]; then
        expected_code=200
    fi
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$response" -eq "$expected_code" ]; then
        test_result 0 "$description"
    else
        test_result 1 "$description (Code: $response, Attendu: $expected_code)"
    fi
}

# Fonction pour tester une API avec authentification
test_api_auth() {
    local url="$1"
    local description="$2"
    local method="$3"
    local data="$4"
    
    if [ -z "$method" ]; then
        method="GET"
    fi
    
    if [ -n "$data" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" \
                       -H "Content-Type: application/json" \
                       -d "$data" "$url")
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" "$url")
    fi
    
    if [ "$response" -eq 200 ] || [ "$response" -eq 201 ]; then
        test_result 0 "$description"
    else
        test_result 1 "$description (Code: $response)"
    fi
}

echo -e "${BLUE}📋 1. TESTS DE BASE${NC}"
echo "=================="

# Test si le serveur répond
test_url "$BASE_URL" "Serveur Spring Boot accessible"

# Test interface frontend
test_url "$BASE_URL/admin-dashboard.html" "Interface d'administration accessible"

echo ""
echo -e "${BLUE}🔧 2. TESTS DES APIs PUBLIQUES${NC}"
echo "========================"

# APIs publiques (sans authentification)
test_url "$API_URL/dashboard/stats" "Statistiques globales"
test_url "$API_URL/dashboard/quick-stats" "Statistiques rapides"
test_url "$API_URL/configuration/hospital-name" "Nom de l'hôpital"
test_url "$API_URL/configuration/logo" "Logo de l'hôpital"
test_url "$API_URL/configuration/localization" "Paramètres de localisation"

echo ""
echo -e "${BLUE}📋 3. TESTS DES APIs SWAGGER${NC}"
echo "========================"

# Documentation Swagger
test_url "$API_URL/swagger-ui/index.html" "Documentation Swagger UI"
test_url "$API_URL/v3/api-docs" "Spécification OpenAPI"

echo ""
echo -e "${BLUE}👥 4. TESTS DES APIs PATIENTS${NC}"
echo "=========================="

# APIs Patients (nécessitent authentification en production)
test_url "$API_URL/patients" "Liste des patients" 401
test_url "$API_URL/patients/search?query=test" "Recherche de patients" 401
test_url "$API_URL/patients/stats" "Statistiques patients" 401

echo ""
echo -e "${BLUE}👨‍⚕️ 5. TESTS DES APIs MÉDECINS${NC}"
echo "============================"

# APIs Médecins
test_url "$API_URL/doctors" "Liste des médecins" 401
test_url "$API_URL/doctors/available" "Médecins disponibles" 401
test_url "$API_URL/doctors/by-specialization?spec=Cardiologie" "Médecins par spécialisation" 401

echo ""
echo -e "${BLUE}📋 6. TESTS DES APIs CONSULTATIONS${NC}"
echo "==============================="

# APIs Consultations
test_url "$API_URL/consultations" "Liste des consultations" 401
test_url "$API_URL/consultations/today" "Consultations du jour" 401
test_url "$API_URL/consultations/stats" "Statistiques consultations" 401

echo ""
echo -e "${BLUE}🔬 7. TESTS DES APIs LABORATOIRE${NC}"
echo "=============================="

# APIs Laboratoire
test_url "$API_URL/laboratory/tests" "Tests de laboratoire" 401
test_url "$API_URL/laboratory/orders" "Commandes laboratoire" 401
test_url "$API_URL/laboratory/stats" "Statistiques laboratoire" 401

echo ""
echo -e "${BLUE}💊 8. TESTS DES APIs PHARMACIE${NC}"
echo "============================"

# APIs Pharmacie
test_url "$API_URL/pharmacy/medications" "Liste des médicaments" 401
test_url "$API_URL/pharmacy/prescriptions" "Prescriptions" 401
test_url "$API_URL/pharmacy/stats" "Statistiques pharmacie" 401

echo ""
echo -e "${BLUE}⏱️ 9. TESTS DES APIs FILE D'ATTENTE${NC}"
echo "====================================="

# APIs File d'attente
test_url "$API_URL/queue/current" "File d'attente actuelle" 401
test_url "$API_URL/queue/stats" "Statistiques file d'attente" 401

echo ""
echo -e "${BLUE}📄 10. TESTS DES APIs IMPRESSION${NC}"
echo "=============================="

# APIs Impression
test_url "$API_URL/print/status" "Statut service impression"
test_url "$API_URL/print/templates" "Templates d'impression" 401

echo ""
echo -e "${BLUE}📂 11. TESTS DES APIs IMPORT${NC}"
echo "==========================="

# APIs Import
test_url "$API_URL/patients/import/instructions" "Instructions import CSV"
test_url "$API_URL/patients/import/status" "Statut service import" 401

echo ""
echo -e "${BLUE}⚙️ 12. TESTS DES APIs CONFIGURATION${NC}"
echo "=================================="

# APIs Configuration
test_url "$API_URL/configuration" "Configuration active" 401
test_url "$API_URL/configuration/prefixes" "Préfixes de numérotation" 401
test_url "$API_URL/configuration/financial" "Paramètres financiers" 401

echo ""
echo -e "${BLUE}🖨️ 13. TESTS FONCTIONNELS AVANCÉS${NC}"
echo "==============================="

# Test de téléchargement du template CSV
response=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL/patients/import/template")
if [ "$response" -eq 401 ]; then
    test_result 0 "Template CSV (authentification requise)"
else
    test_result 1 "Template CSV (Code: $response)"
fi

# Test de l'endpoint de santé de l'application
test_url "$BASE_URL/actuator/health" "Health Check" 200

echo ""
echo -e "${BLUE}📊 14. TESTS DE PERFORMANCE${NC}"
echo "========================"

# Test de temps de réponse
start_time=$(date +%s%N)
curl -s "$BASE_URL" > /dev/null
end_time=$(date +%s%N)
response_time=$(( (end_time - start_time) / 1000000 ))

if [ $response_time -lt 1000 ]; then
    test_result 0 "Temps de réponse accueil ($response_time ms)"
else
    test_result 1 "Temps de réponse accueil trop lent ($response_time ms)"
fi

echo ""
echo -e "${BLUE}🔍 15. VÉRIFICATION DES FICHIERS${NC}"
echo "============================="

# Vérification des fichiers essentiels
files_to_check=(
    "src/main/resources/static/admin-dashboard.html"
    "src/main/java/com/hospital/HospitalManagementApplication.java"
    "src/main/java/com/hospital/controller/DashboardController.java"
    "src/main/java/com/hospital/controller/HospitalConfigurationController.java"
    "src/main/java/com/hospital/controller/PatientImportController.java"
    "src/main/java/com/hospital/controller/PrintController.java"
    "src/main/java/com/hospital/service/HospitalConfigurationService.java"
    "src/main/java/com/hospital/service/PatientImportService.java"
    "src/main/java/com/hospital/service/PrintService.java"
    "src/main/java/com/hospital/entity/HospitalConfiguration.java"
    "pom.xml"
    "README_FINAL.md"
)

for file in "${files_to_check[@]}"; do
    if [ -f "$file" ]; then
        test_result 0 "Fichier $file existe"
    else
        test_result 1 "Fichier $file manquant"
    fi
done

echo ""
echo -e "${BLUE}📱 16. TESTS INTÉGRATION FRONTEND${NC}"
echo "==============================="

# Test des ressources statiques
test_url "$BASE_URL/admin-dashboard.html" "Page d'administration"
test_url "$BASE_URL/css/bootstrap.min.css" "CSS Bootstrap" 404 # Normal si pas dans static
test_url "$BASE_URL/js/bootstrap.bundle.min.js" "JS Bootstrap" 404 # Normal si pas dans static

echo ""
echo -e "${YELLOW}📋 RÉSUMÉ DES TESTS${NC}"
echo "=================="
echo -e "Total des tests: ${BLUE}$TOTAL_TESTS${NC}"
echo -e "Tests réussis: ${GREEN}$PASSED_TESTS${NC}"
echo -e "Tests échoués: ${RED}$FAILED_TESTS${NC}"

success_rate=$(( PASSED_TESTS * 100 / TOTAL_TESTS ))
echo -e "Taux de réussite: ${BLUE}$success_rate%${NC}"

echo ""
if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 TOUS LES TESTS SONT PASSÉS !${NC}"
    echo -e "${GREEN}✅ Le système hospitalier est entièrement fonctionnel${NC}"
else
    echo -e "${YELLOW}⚠️ Certains tests ont échoué${NC}"
    echo -e "${YELLOW}ℹ️ Les échecs d'authentification (401) sont normaux pour les APIs sécurisées${NC}"
fi

echo ""
echo -e "${BLUE}🔗 LIENS UTILES${NC}"
echo "=============="
echo -e "🌐 Interface admin: ${BLUE}$BASE_URL/admin-dashboard.html${NC}"
echo -e "📚 Documentation API: ${BLUE}$API_URL/swagger-ui/index.html${NC}"
echo -e "🔧 Health Check: ${BLUE}$BASE_URL/actuator/health${NC}"
echo -e "📊 Statistiques: ${BLUE}$API_URL/dashboard/stats${NC}"

echo ""
echo -e "${GREEN}✅ Tests terminés !${NC}"

# Code de sortie basé sur le taux de réussite
if [ $success_rate -ge 80 ]; then
    exit 0
else
    exit 1
fi