package com.hospital.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class InstallationService {

    @Autowired(required = false)
    private DataSource dataSource;

    private static final String INSTALL_LOCK_FILE = "install.lock";
    private static final String CONFIG_FILE = "application-production.yml";

    /**
     * Vérifier les prérequis système
     */
    public Map<String, Boolean> checkRequirements() {
        Map<String, Boolean> requirements = new HashMap<>();
        
        // Vérifier Java
        String javaVersion = System.getProperty("java.version");
        boolean javaOk = javaVersion.startsWith("17") || javaVersion.startsWith("18") || 
                        javaVersion.startsWith("19") || javaVersion.startsWith("20") ||
                        javaVersion.startsWith("21");
        requirements.put("java", javaOk);
        
        // Vérifier l'espace disque (minimum 1GB)
        File currentDir = new File(".");
        long freeSpace = currentDir.getFreeSpace();
        boolean diskSpaceOk = freeSpace > 1024 * 1024 * 1024; // 1GB
        requirements.put("diskSpace", diskSpaceOk);
        
        // Vérifier les permissions d'écriture
        boolean writePermissionOk = currentDir.canWrite();
        requirements.put("writePermission", writePermissionOk);
        
        // Vérifier la mémoire disponible
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        boolean memoryOk = maxMemory > 512 * 1024 * 1024; // 512MB minimum
        requirements.put("memory", memoryOk);
        
        log.info("Vérification des prérequis - Java: {}, Espace disque: {}, Permissions: {}, Mémoire: {}", 
                javaOk, diskSpaceOk, writePermissionOk, memoryOk);
        
        return requirements;
    }

    /**
     * Tester la connexion à la base de données
     */
    public boolean testDatabaseConnection(String host, String port, String database, 
                                        String username, String password, String type) {
        String url = buildDatabaseUrl(host, port, database, type);
        
        try {
            // Charger le driver approprié
            loadDatabaseDriver(type);
            
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                return connection.isValid(5); // Timeout de 5 secondes
            }
        } catch (Exception e) {
            log.error("Erreur de connexion à la base de données: ", e);
            return false;
        }
    }

    /**
     * Créer les tables de la base de données
     */
    public boolean createTables(Map<String, String> config) {
        try {
            String url = buildDatabaseUrl(config.get("host"), config.get("port"), 
                                        config.get("database"), config.get("type"));
            
            loadDatabaseDriver(config.get("type"));
            
            try (Connection connection = DriverManager.getConnection(url, 
                    config.get("username"), config.get("password"))) {
                
                // Lire le script SQL de création des tables
                String createTablesScript = readSqlScript("sql/create-tables.sql");
                
                // Exécuter le script
                try (Statement statement = connection.createStatement()) {
                    String[] statements = createTablesScript.split(";");
                    for (String stmt : statements) {
                        if (stmt.trim().length() > 0) {
                            statement.execute(stmt.trim());
                        }
                    }
                }
                
                log.info("Tables créées avec succès");
                return true;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création des tables: ", e);
            return false;
        }
    }

    /**
     * Insérer les données initiales
     */
    public boolean insertInitialData(Map<String, String> config) {
        try {
            String url = buildDatabaseUrl(config.get("host"), config.get("port"), 
                                        config.get("database"), config.get("type"));
            
            try (Connection connection = DriverManager.getConnection(url, 
                    config.get("username"), config.get("password"))) {
                
                // Lire le script SQL des données initiales
                String insertDataScript = readSqlScript("sql/initial-data.sql");
                
                // Exécuter le script
                try (Statement statement = connection.createStatement()) {
                    String[] statements = insertDataScript.split(";");
                    for (String stmt : statements) {
                        if (stmt.trim().length() > 0) {
                            statement.execute(stmt.trim());
                        }
                    }
                }
                
                log.info("Données initiales insérées avec succès");
                return true;
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'insertion des données initiales: ", e);
            return false;
        }
    }

    /**
     * Créer l'utilisateur administrateur
     */
    public boolean createAdminUser(String username, String password, String email, 
                                 String firstName, String lastName, Map<String, String> dbConfig) {
        try {
            String url = buildDatabaseUrl(dbConfig.get("host"), dbConfig.get("port"), 
                                        dbConfig.get("database"), dbConfig.get("type"));
            
            try (Connection connection = DriverManager.getConnection(url, 
                    dbConfig.get("username"), dbConfig.get("password"))) {
                
                // Vérifier si l'utilisateur existe déjà
                String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            log.info("L'utilisateur admin existe déjà");
                            return true;
                        }
                    }
                }
                
                // Créer l'utilisateur admin
                String insertSql = """
                    INSERT INTO users (username, password, email, first_name, last_name, role, active, created_at) 
                    VALUES (?, ?, ?, ?, ?, 'ADMIN', true, CURRENT_TIMESTAMP)
                    """;
                
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, encodePassword(password)); // Simple encoding, should use BCrypt in production
                    insertStmt.setString(3, email);
                    insertStmt.setString(4, firstName);
                    insertStmt.setString(5, lastName);
                    
                    int affected = insertStmt.executeUpdate();
                    log.info("Utilisateur admin créé: {} lignes affectées", affected);
                    return affected > 0;
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur admin: ", e);
            return false;
        }
    }

    /**
     * Créer le fichier de configuration
     */
    public boolean createConfigurationFile(Map<String, String> config) {
        try {
            String configContent = generateConfigurationContent(config);
            
            Path configPath = Paths.get(CONFIG_FILE);
            Files.write(configPath, configContent.getBytes(StandardCharsets.UTF_8));
            
            log.info("Fichier de configuration créé: {}", CONFIG_FILE);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de la création du fichier de configuration: ", e);
            return false;
        }
    }

    /**
     * Marquer l'installation comme terminée
     */
    public void markInstallationComplete() {
        try {
            Path lockPath = Paths.get(INSTALL_LOCK_FILE);
            String lockContent = "Installation completed at: " + new java.util.Date();
            Files.write(lockPath, lockContent.getBytes(StandardCharsets.UTF_8));
            log.info("Installation marquée comme terminée");
        } catch (Exception e) {
            log.error("Erreur lors de la création du fichier de verrouillage: ", e);
        }
    }

    /**
     * Vérifier si l'installation est terminée
     */
    public boolean isInstallationComplete() {
        Path lockPath = Paths.get(INSTALL_LOCK_FILE);
        return Files.exists(lockPath);
    }

    // Méthodes utilitaires privées

    private String buildDatabaseUrl(String host, String port, String database, String type) {
        return switch (type.toLowerCase()) {
            case "postgresql" -> "jdbc:postgresql://" + host + ":" + port + "/" + database;
            case "mysql" -> "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";
            case "h2" -> "jdbc:h2:mem:" + database + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
            default -> throw new IllegalArgumentException("Type de base de données non supporté: " + type);
        };
    }

    private void loadDatabaseDriver(String type) throws ClassNotFoundException {
        switch (type.toLowerCase()) {
            case "postgresql" -> Class.forName("org.postgresql.Driver");
            case "mysql" -> Class.forName("com.mysql.cj.jdbc.Driver");
            case "h2" -> Class.forName("org.h2.Driver");
            default -> throw new IllegalArgumentException("Type de base de données non supporté: " + type);
        }
    }

    private String readSqlScript(String resourcePath) throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Si le fichier n'existe pas, retourner un script par défaut
            return generateDefaultSqlScript();
        }
    }

    private String generateDefaultSqlScript() {
        return """
            -- Script SQL par défaut pour la création des tables
            CREATE TABLE IF NOT EXISTS users (
                id BIGSERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                first_name VARCHAR(50),
                last_name VARCHAR(50),
                role VARCHAR(20) DEFAULT 'USER',
                active BOOLEAN DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS patients (
                id BIGSERIAL PRIMARY KEY,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                date_of_birth DATE,
                gender VARCHAR(10),
                phone VARCHAR(20),
                email VARCHAR(100),
                address TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS doctors (
                id BIGSERIAL PRIMARY KEY,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                specialization VARCHAR(100),
                phone VARCHAR(20),
                email VARCHAR(100),
                license_number VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;
    }

    private String generateConfigurationContent(Map<String, String> config) {
        return String.format("""
            server:
              port: 8080
              servlet:
                context-path: /hospital
            
            spring:
              datasource:
                url: %s
                username: %s
                password: %s
                driver-class-name: %s
              
              jpa:
                hibernate:
                  ddl-auto: validate
                show-sql: false
                properties:
                  hibernate:
                    dialect: %s
                    format_sql: true
            
              security:
                jwt:
                  secret: %s
                  expiration: 86400000
            
            logging:
              level:
                com.hospital: INFO
                org.springframework.security: DEBUG
              file:
                name: logs/hospital-system.log
            
            management:
              endpoints:
                web:
                  exposure:
                    include: health,info,metrics
            """,
            buildDatabaseUrl(config.get("host"), config.get("port"), config.get("database"), config.get("type")),
            config.get("username"),
            config.get("password"),
            getDriverClassName(config.get("type")),
            getHibernateDialect(config.get("type")),
            generateJwtSecret()
        );
    }

    private String getDriverClassName(String type) {
        return switch (type.toLowerCase()) {
            case "postgresql" -> "org.postgresql.Driver";
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "h2" -> "org.h2.Driver";
            default -> "org.postgresql.Driver";
        };
    }

    private String getHibernateDialect(String type) {
        return switch (type.toLowerCase()) {
            case "postgresql" -> "org.hibernate.dialect.PostgreSQLDialect";
            case "mysql" -> "org.hibernate.dialect.MySQLDialect";
            case "h2" -> "org.hibernate.dialect.H2Dialect";
            default -> "org.hibernate.dialect.PostgreSQLDialect";
        };
    }

    private String generateJwtSecret() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    private String encodePassword(String password) {
        // Simple encoding pour la démonstration, utilisez BCrypt en production
        return "{noop}" + password;
    }
}