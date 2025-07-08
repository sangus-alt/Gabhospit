#!/bin/bash

# 🏥 SCRIPT DE DÉMARRAGE - SYSTÈME D'INFORMATION HOSPITALIER
# ============================================================

echo "🏥 DÉMARRAGE DU SYSTÈME D'INFORMATION HOSPITALIER"
echo "=================================================="
echo ""

# Vérification de Java
echo "🔍 Vérification de Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Veuillez installer Java 17 ou supérieur."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' | head -1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 ou supérieur requis. Version détectée: $JAVA_VERSION"
    exit 1
fi
echo "✅ Java $JAVA_VERSION détecté"

# Vérification de Maven
echo "🔍 Vérification de Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé. Veuillez installer Maven 3.8 ou supérieur."
    exit 1
fi
echo "✅ Maven détecté"

# Nettoyage et compilation
echo ""
echo "🧹 Nettoyage et compilation du projet..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation"
    exit 1
fi
echo "✅ Compilation réussie"

# Démarrage de l'application
echo ""
echo "🚀 Démarrage de l'application Spring Boot..."
echo "📍 L'application sera disponible sur: http://localhost:8080"
echo "🔐 Compte administrateur:"
echo "   👤 Utilisateur: admin"
echo "   🔒 Mot de passe: Admin123!"
echo ""
echo "🌐 Interfaces disponibles:"
echo "   📊 Dashboard Admin: http://localhost:8080/admin-dashboard.html"
echo "   🏠 Page d'accueil: http://localhost:8080/"
echo "   📚 Documentation API: http://localhost:8080/swagger-ui.html"
echo ""
echo "⚡ Démarrage en cours... (cela peut prendre quelques secondes)"
echo "📝 Logs en temps réel:"
echo "---------------------------------------------------"

# Démarrage avec profil de développement
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# En cas d'arrêt
echo ""
echo "🛑 Application arrêtée"
echo "💡 Pour redémarrer, exécutez: ./start-hospital-system.sh"