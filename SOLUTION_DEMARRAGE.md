# 🚨 **SOLUTION PROBLÈME DE DÉMARRAGE**

## 🔍 **PROBLÈME IDENTIFIÉ**

Vous avez rencontré l'erreur : `mvn: command not found`

**Causes possibles :**
1. ❌ Vous n'êtes pas dans le bon répertoire
2. ❌ Le chemin vers Maven n'est pas configuré correctement
3. ❌ Confusion entre `/workspace` et `/home/server/workspace`

## ✅ **SOLUTION IMMÉDIATE**

### **🎯 Méthode 1 : Script Corrigé (RECOMMANDÉ)**

```bash
# Depuis N'IMPORTE QUEL répertoire :
/workspace/start-fixed.sh
```

### **🎯 Méthode 2 : Démarrage Manuel**

```bash
# 1. Aller dans le bon répertoire
cd /workspace

# 2. Utiliser le chemin complet vers Maven
/usr/bin/mvn clean compile

# 3. Construire le JAR
/usr/bin/mvn package -DskipTests

# 4. Démarrer l'application
/usr/bin/java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev
```

### **🎯 Méthode 3 : Une seule commande**

```bash
cd /workspace && /usr/bin/java -jar target/hospital-management-system-1.0.0.jar --spring.profiles.active=dev
```

## 📍 **IMPORTANT : RÉPERTOIRE CORRECT**

⚠️ **Le projet se trouve dans :** `/workspace`  
❌ **PAS dans :** `/home/server/workspace`

### **Vérification rapide :**
```bash
ls -la /workspace/pom.xml
# Doit afficher le fichier pom.xml
```

## 🌐 **ACCÈS APRÈS DÉMARRAGE**

Une fois l'application démarrée, ouvrez votre navigateur :

- **🏠 Application :** http://localhost:8080
- **🗄️ Console H2 :** http://localhost:8080/h2-console  
- **📚 Documentation :** http://localhost:8080/swagger-ui/index.html
- **🎛️ Dashboard :** http://localhost:8080/admin-dashboard.html

## 🔑 **IDENTIFIANTS**

- **👤 Admin :** `admin` / `Admin123!`
- **🗄️ H2 Database :**
  - JDBC URL : `jdbc:h2:mem:hospital_db`
  - Username : `sa`
  - Password : *(vide)*

## 🆘 **EN CAS D'ÉCHEC**

Si rien ne fonctionne, exécutez cette commande de diagnostic :

```bash
echo "=== DIAGNOSTIC ==="
echo "Répertoire actuel: $(pwd)"
echo "Fichier pom.xml: $(ls -la /workspace/pom.xml 2>/dev/null || echo 'NON TROUVÉ')"
echo "Maven: $(which mvn || echo 'NON TROUVÉ')"
echo "Java: $(which java || echo 'NON TROUVÉ')"
echo "JAR: $(ls -la /workspace/target/hospital-management-system-1.0.0.jar 2>/dev/null || echo 'NON TROUVÉ')"
```

---

## 🎉 **DÉMARRAGE RÉUSSI ?**

Quand vous voyez ce message, c'est bon ! :
```
Started HospitalManagementSystemApplication in X.XXX seconds
```

**🏥 Votre système hospitalier est opérationnel !**