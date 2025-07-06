🏥 SYSTÈME D'INFORMATION HOSPITALIER - DÉMARRAGE RAPIDE
========================================================

PROBLÈME RÉSOLU: "mvn: command not found"

SOLUTIONS (choisissez UNE méthode) :

=== MÉTHODE 1 : Script corrigé (FACILE) ===
/workspace/start-fixed.sh

=== MÉTHODE 2 : Script ultra-simple ===
/workspace/demarrage-simple.sh

=== MÉTHODE 3 : Commande directe ===
cd /workspace && /usr/bin/java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev

=== ACCÈS APPLICATION ===
http://localhost:8080

Compte admin: admin / Admin123!

=== IMPORTANT ===
- Le projet est dans /workspace (PAS dans /home/server/workspace)
- Utilisez les chemins complets: /usr/bin/mvn et /usr/bin/java
- Si le JAR n'existe pas, recompilez d'abord avec: /usr/bin/mvn package -DskipTests

=== DIAGNOSTIC ===
ls -la /workspace/pom.xml
ls -la /workspace/target/hospital-management-system-1.0.0.jar