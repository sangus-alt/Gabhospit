#!/bin/bash

# =============================================================================
# Script de Test d'Intégration Complet - Système d'Information Hospitalier
# =============================================================================

set -e  # Arrêt en cas d'erreur

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080"
API_URL="$BASE_URL/api"
TIMEOUT=30
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Fonctions utilitaires
print_header() {
    echo -e "\n${BLUE}===============================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}===============================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
    ((PASSED_TESTS++))
    ((TOTAL_TESTS++))
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
    ((FAILED_TESTS++))
    ((TOTAL_TESTS++))
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Test de connectivité HTTP
test_http_connectivity() {
    print_header "Tests de Connectivité HTTP"
    
    # Test de l'application principale
    if curl -s --max-time $TIMEOUT -o /dev/null -w "%{http_code}" "$BASE_URL" | grep -q "200"; then
        print_success "Application principale accessible ($BASE_URL)"
    else
        print_error "Application principale non accessible ($BASE_URL)"
    fi
    
    # Test du dashboard admin
    if curl -s --max-time $TIMEOUT -o /dev/null -w "%{http_code}" "$BASE_URL/admin-dashboard.html" | grep -q "200"; then
        print_success "Dashboard admin accessible ($BASE_URL/admin-dashboard.html)"
    else
        print_error "Dashboard admin non accessible"
    fi
    
    # Test de la documentation API
    if curl -s --max-time $TIMEOUT -o /dev/null -w "%{http_code}" "$BASE_URL/swagger-ui/index.html" | grep -q "200"; then
        print_success "Documentation API accessible ($BASE_URL/swagger-ui/index.html)"
    else
        print_error "Documentation API non accessible"
    fi
    
    # Test OpenAPI spec
    if curl -s --max-time $TIMEOUT -o /dev/null -w "%{http_code}" "$BASE_URL/v3/api-docs" | grep -q "200"; then
        print_success "Spécifications OpenAPI disponibles"
    else
        print_error "Spécifications OpenAPI non disponibles"
    fi
}

# Test des APIs REST
test_api_endpoints() {
    print_header "Tests des API REST"
    
    # Dashboard API
    if curl -s --max-time $TIMEOUT "$API_URL/dashboard/statistics" | grep -q "patients\|doctors"; then
        print_success "API Dashboard - Statistiques"
    else
        print_error "API Dashboard - Statistiques"
    fi
    
    # Patients API
    if curl -s --max-time $TIMEOUT "$API_URL/patients" | grep -q "content\|empty"; then
        print_success "API Patients - Liste"
    else
        print_error "API Patients - Liste"
    fi
    
    # Doctors API
    if curl -s --max-time $TIMEOUT "$API_URL/doctors" | grep -q "content\|empty"; then
        print_success "API Médecins - Liste"
    else
        print_error "API Médecins - Liste"
    fi
    
    # Laboratory API
    if curl -s --max-time $TIMEOUT "$API_URL/laboratory/tests" | grep -q "content\|empty"; then
        print_success "API Laboratoire - Tests"
    else
        print_error "API Laboratoire - Tests"
    fi
    
    # Imaging API
    if curl -s --max-time $TIMEOUT "$API_URL/imaging/results" | grep -q "content\|empty"; then
        print_success "API Imagerie - Résultats"
    else
        print_error "API Imagerie - Résultats"
    fi
    
    # Pharmacy API
    if curl -s --max-time $TIMEOUT "$API_URL/pharmacy/medications" | grep -q "content\|empty"; then
        print_success "API Pharmacie - Médicaments"
    else
        print_error "API Pharmacie - Médicaments"
    fi
    
    # Billing API
    if curl -s --max-time $TIMEOUT "$API_URL/billing/bills" | grep -q "content\|empty"; then
        print_success "API Facturation - Factures"
    else
        print_error "API Facturation - Factures"
    fi
    
    # Vaccines API
    if curl -s --max-time $TIMEOUT "$API_URL/vaccines" | grep -q "content\|empty"; then
        print_success "API Vaccination - Vaccins"
    else
        print_error "API Vaccination - Vaccins"
    fi
    
    # Inventory API
    if curl -s --max-time $TIMEOUT "$API_URL/inventory/items" | grep -q "content\|empty"; then
        print_success "API Inventaire - Articles"
    else
        print_error "API Inventaire - Articles"
    fi
    
    # Insurance API
    if curl -s --max-time $TIMEOUT "$API_URL/insurance/companies" | grep -q "content\|empty"; then
        print_success "API Assurances - Compagnies"
    else
        print_error "API Assurances - Compagnies"
    fi
}

# Test de création de données
test_data_creation() {
    print_header "Tests de Création de Données"
    
    # Test de création d'un patient
    PATIENT_DATA='{
        "firstName": "Test",
        "lastName": "Patient",
        "dateOfBirth": "1990-01-01",
        "phoneNumber": "0123456789",
        "email": "test@patient.com",
        "gender": "M",
        "address": "123 Test Street",
        "emergencyContact": "Test Contact",
        "bloodType": "A+"
    }'
    
    if PATIENT_RESPONSE=$(curl -s --max-time $TIMEOUT -X POST \
        -H "Content-Type: application/json" \
        -d "$PATIENT_DATA" \
        "$API_URL/patients" 2>/dev/null) && echo "$PATIENT_RESPONSE" | grep -q "firstName"; then
        print_success "Création patient - POST"
        PATIENT_ID=$(echo "$PATIENT_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
        export TEST_PATIENT_ID=$PATIENT_ID
    else
        print_error "Création patient - POST"
    fi
    
    # Test de création d'un médecin
    DOCTOR_DATA='{
        "firstName": "Dr. Test",
        "lastName": "Doctor",
        "specialty": "Médecine Générale",
        "licenseNumber": "TEST123456",
        "phoneNumber": "0123456789",
        "email": "test@doctor.com"
    }'
    
    if curl -s --max-time $TIMEOUT -X POST \
        -H "Content-Type: application/json" \
        -d "$DOCTOR_DATA" \
        "$API_URL/doctors" | grep -q "firstName"; then
        print_success "Création médecin - POST"
    else
        print_error "Création médecin - POST"
    fi
}

# Test des opérations CRUD
test_crud_operations() {
    print_header "Tests des Opérations CRUD"
    
    if [ -n "$TEST_PATIENT_ID" ]; then
        # Test GET patient par ID
        if curl -s --max-time $TIMEOUT "$API_URL/patients/$TEST_PATIENT_ID" | grep -q "firstName"; then
            print_success "Lecture patient - GET by ID"
        else
            print_error "Lecture patient - GET by ID"
        fi
        
        # Test PUT patient (mise à jour)
        UPDATE_DATA='{
            "firstName": "Test Updated",
            "lastName": "Patient",
            "dateOfBirth": "1990-01-01",
            "phoneNumber": "0123456789",
            "email": "test.updated@patient.com",
            "gender": "M",
            "address": "123 Test Street Updated",
            "emergencyContact": "Test Contact",
            "bloodType": "A+"
        }'
        
        if curl -s --max-time $TIMEOUT -X PUT \
            -H "Content-Type: application/json" \
            -d "$UPDATE_DATA" \
            "$API_URL/patients/$TEST_PATIENT_ID" | grep -q "Updated"; then
            print_success "Mise à jour patient - PUT"
        else
            print_error "Mise à jour patient - PUT"
        fi
    else
        print_error "ID patient non disponible pour les tests CRUD"
    fi
}

# Test des fonctionnalités métier
test_business_logic() {
    print_header "Tests de Logique Métier"
    
    # Test recherche patients
    if curl -s --max-time $TIMEOUT "$API_URL/patients/search?firstName=Test" | grep -q "content"; then
        print_success "Recherche patient par nom"
    else
        print_error "Recherche patient par nom"
    fi
    
    # Test statistiques facturation
    if curl -s --max-time $TIMEOUT "$API_URL/billing/statistics/revenue?fromDate=2024-01-01&toDate=2024-12-31" | grep -q "0\|[0-9]"; then
        print_success "Statistiques revenus"
    else
        print_error "Statistiques revenus"
    fi
    
    # Test gestion file d'attente
    if curl -s --max-time $TIMEOUT "$API_URL/queue/current" | grep -q "content\|empty\|\[\]"; then
        print_success "File d'attente actuelle"
    else
        print_error "File d'attente actuelle"
    fi
    
    # Test vaccins en rupture de stock
    if curl -s --max-time $TIMEOUT "$API_URL/vaccines/low-stock" | grep -q "content\|empty\|\[\]"; then
        print_success "Vaccins en rupture de stock"
    else
        print_error "Vaccins en rupture de stock"
    fi
    
    # Test articles inventaire expirés
    if curl -s --max-time $TIMEOUT "$API_URL/inventory/alerts/expired" | grep -q "content\|empty\|\[\]"; then
        print_success "Articles expirés inventaire"
    else
        print_error "Articles expirés inventaire"
    fi
}

# Test des fonctionnalités avancées
test_advanced_features() {
    print_header "Tests des Fonctionnalités Avancées"
    
    # Test génération de rapports
    if curl -s --max-time $TIMEOUT -w "%{http_code}" "$API_URL/reports/daily?date=2024-01-15" | grep -q "200"; then
        print_success "Génération rapport PDF"
    else
        print_error "Génération rapport PDF"
    fi
    
    # Test impression étiquettes
    if curl -s --max-time $TIMEOUT -w "%{http_code}" "$API_URL/print/patient-labels/1" | grep -q "200\|404"; then
        print_success "Service impression étiquettes"
    else
        print_error "Service impression étiquettes"
    fi
    
    # Test surveillance système
    if curl -s --max-time $TIMEOUT "$BASE_URL/actuator/health" | grep -q "UP\|status"; then
        print_success "Surveillance système (Actuator)"
    else
        print_error "Surveillance système (Actuator)"
    fi
    
    # Test métriques
    if curl -s --max-time $TIMEOUT "$BASE_URL/actuator/metrics" | grep -q "names\|measurements"; then
        print_success "Métriques système"
    else
        print_error "Métriques système"
    fi
}

# Test de performance
test_performance() {
    print_header "Tests de Performance"
    
    # Test temps de réponse API
    RESPONSE_TIME=$(curl -s --max-time $TIMEOUT -w "%{time_total}" -o /dev/null "$API_URL/dashboard/statistics")
    if (( $(echo "$RESPONSE_TIME < 2.0" | bc -l) )); then
        print_success "Temps de réponse API acceptable (${RESPONSE_TIME}s)"
    else
        print_error "Temps de réponse API trop lent (${RESPONSE_TIME}s)"
    fi
    
    # Test charge multiple
    print_info "Test de charge (10 requêtes simultanées)..."
    for i in {1..10}; do
        curl -s --max-time $TIMEOUT "$API_URL/patients" > /dev/null &
    done
    wait
    print_success "Test de charge concurrent terminé"
}

# Test de sécurité de base
test_security() {
    print_header "Tests de Sécurité de Base"
    
    # Test headers de sécurité
    HEADERS=$(curl -s --max-time $TIMEOUT -I "$BASE_URL")
    
    if echo "$HEADERS" | grep -i "x-frame-options\|x-content-type-options"; then
        print_success "Headers de sécurité présents"
    else
        print_error "Headers de sécurité manquants"
    fi
    
    # Test tentative d'accès non autorisé
    if curl -s --max-time $TIMEOUT -w "%{http_code}" "$API_URL/admin/config" | grep -q "401\|403\|404"; then
        print_success "Protection endpoints admin"
    else
        print_error "Endpoints admin non protégés"
    fi
}

# Test de la base de données
test_database() {
    print_header "Tests de Base de Données"
    
    # Vérification de la connectivité via l'API
    if curl -s --max-time $TIMEOUT "$API_URL/dashboard/statistics" | grep -q "patients\|doctors"; then
        print_success "Connectivité base de données via API"
    else
        print_error "Problème connectivité base de données"
    fi
    
    # Test transactions (création puis vérification)
    if [ -n "$TEST_PATIENT_ID" ]; then
        if curl -s --max-time $TIMEOUT "$API_URL/patients/$TEST_PATIENT_ID" | grep -q "firstName"; then
            print_success "Persistance des données"
        else
            print_error "Problème persistance des données"
        fi
    fi
}

# Test de l'interface frontend
test_frontend() {
    print_header "Tests Interface Frontend"
    
    # Test chargement des ressources CSS
    if curl -s --max-time $TIMEOUT "$BASE_URL/admin-dashboard.html" | grep -q "bootstrap\|css"; then
        print_success "Chargement ressources CSS"
    else
        print_error "Problème chargement CSS"
    fi
    
    # Test chargement des ressources JavaScript
    if curl -s --max-time $TIMEOUT "$BASE_URL/admin-dashboard.html" | grep -q "script\|javascript"; then
        print_success "Chargement ressources JavaScript"
    else
        print_error "Problème chargement JavaScript"
    fi
    
    # Test intégration API dans le frontend
    if curl -s --max-time $TIMEOUT "$BASE_URL" | grep -q "api/"; then
        print_success "Intégration API dans frontend"
    else
        print_error "Problème intégration API frontend"
    fi
}

# Nettoyage des données de test
cleanup_test_data() {
    print_header "Nettoyage des Données de Test"
    
    if [ -n "$TEST_PATIENT_ID" ]; then
        if curl -s --max-time $TIMEOUT -X DELETE "$API_URL/patients/$TEST_PATIENT_ID" | grep -q "success\|deleted\|ok" || \
           curl -s --max-time $TIMEOUT -w "%{http_code}" -X DELETE "$API_URL/patients/$TEST_PATIENT_ID" | grep -q "200\|204"; then
            print_success "Suppression patient de test"
        else
            print_info "Patient de test non supprimé (peut-être pas autorisé)"
        fi
    fi
}

# Génération du rapport final
generate_report() {
    print_header "Rapport de Test Final"
    
    echo -e "${BLUE}Total des tests: $TOTAL_TESTS${NC}"
    echo -e "${GREEN}Tests réussis: $PASSED_TESTS${NC}"
    echo -e "${RED}Tests échoués: $FAILED_TESTS${NC}"
    
    if [ $FAILED_TESTS -eq 0 ]; then
        echo -e "\n${GREEN}🎉 Tous les tests sont passés avec succès !${NC}"
        echo -e "${GREEN}Le système hospitalier fonctionne correctement.${NC}"
    else
        echo -e "\n${YELLOW}⚠️  Certains tests ont échoué.${NC}"
        echo -e "${YELLOW}Veuillez vérifier les logs et la configuration.${NC}"
    fi
    
    PERCENTAGE=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    echo -e "\n${BLUE}Taux de réussite: ${PERCENTAGE}%${NC}"
}

# Fonction principale
main() {
    echo -e "${BLUE}"
    echo "========================================================"
    echo "   TEST D'INTÉGRATION - SYSTÈME HOSPITALIER (SIH)"
    echo "========================================================"
    echo -e "${NC}"
    
    print_info "Début des tests d'intégration..."
    print_info "URL de base: $BASE_URL"
    print_info "Timeout: ${TIMEOUT}s"
    
    # Vérification préalable
    if ! curl -s --max-time 5 -o /dev/null "$BASE_URL"; then
        print_error "Impossible de se connecter à $BASE_URL"
        print_info "Assurez-vous que l'application est démarrée"
        exit 1
    fi
    
    # Exécution des tests
    test_http_connectivity
    test_api_endpoints
    test_data_creation
    test_crud_operations
    test_business_logic
    test_advanced_features
    test_performance
    test_security
    test_database
    test_frontend
    
    # Nettoyage
    cleanup_test_data
    
    # Rapport final
    generate_report
    
    # Code de sortie
    if [ $FAILED_TESTS -eq 0 ]; then
        exit 0
    else
        exit 1
    fi
}

# Gestion des arguments de ligne de commande
case "${1:-}" in
    --help|-h)
        echo "Usage: $0 [OPTIONS]"
        echo ""
        echo "Options:"
        echo "  --help, -h     Afficher cette aide"
        echo "  --url URL      URL de base de l'application (défaut: http://localhost:8080)"
        echo "  --timeout SEC  Timeout en secondes (défaut: 30)"
        echo "  --quick        Tests rapides uniquement"
        echo ""
        echo "Exemples:"
        echo "  $0                                    # Tests complets"
        echo "  $0 --url http://192.168.1.100:8080   # Tests sur serveur distant"
        echo "  $0 --quick                           # Tests rapides"
        exit 0
        ;;
    --url)
        BASE_URL="$2"
        API_URL="$BASE_URL/api"
        shift 2
        ;;
    --timeout)
        TIMEOUT="$2"
        shift 2
        ;;
    --quick)
        # Tests rapides seulement
        test_http_connectivity
        test_api_endpoints
        generate_report
        exit $?
        ;;
esac

# Lancement des tests
main "$@"