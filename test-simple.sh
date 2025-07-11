#!/bin/bash

echo "=== TEST SIMPLE DU SYSTÈME ==="
echo "1. Vérification du répertoire..."
pwd
ls -la pom.xml 2>/dev/null && echo "✅ pom.xml trouvé" || echo "❌ pom.xml manquant"

echo ""
echo "2. Vérification de Java..."
java -version

echo ""
echo "3. Vérification de Maven..."
mvn -version

echo ""
echo "4. Test de compilation rapide..."
mvn clean compile -q
echo "Code de retour compilation: $?"

echo ""
echo "5. Vérification du JAR..."
ls -la target/hospital-management-system-1.0.0.jar 2>/dev/null && echo "✅ JAR existe" || echo "❌ JAR manquant"

echo ""
echo "6. Test de démarrage (10 secondes)..."
timeout 10s java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev --spring.main.web-environment=false 2>&1 | head -20

echo ""
echo "=== FIN DU TEST ==="