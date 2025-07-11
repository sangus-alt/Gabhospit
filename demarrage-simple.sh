#!/bin/bash
echo "🏥 Démarrage du Système Hospitalier..."
cd /workspace || { echo "❌ Erreur: /workspace non trouvé"; exit 1; }
/usr/bin/java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev