package com.srms.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String dbType = "mysql";
    private static String host = "localhost";
    private static int port = 3306;
    private static String dbName = "srms_db";
    private static String username = "root";
    private static String password = "";
    private static boolean fallbackH2 = true;
    private static String h2Path = "./data/srms_db";

    private static boolean usingH2Fallback = false;
    private static String activeDbInfo = "";

    static {
        loadConfiguration();
    }

    public static void loadConfiguration() {
        Properties props = new Properties();
        try {
            // Check for external configuration file first
            File extFile = new File("db.properties");
            if (extFile.exists()) {
                try (InputStream is = new FileInputStream(extFile)) {
                    props.load(is);
                }
            } else {
                try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (is != null) {
                        props.load(is);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load db.properties, using defaults. " + e.getMessage());
        }

        dbType = props.getProperty("db.type", "mysql").trim();
        host = props.getProperty("db.host", "localhost").trim();
        try {
            port = Integer.parseInt(props.getProperty("db.port", "3306").trim());
        } catch (NumberFormatException ignored) {}
        dbName = props.getProperty("db.name", "srms_db").trim();
        username = props.getProperty("db.username", "root").trim();
        password = props.getProperty("db.password", "");
        fallbackH2 = Boolean.parseBoolean(props.getProperty("db.fallback.h2", "true").trim());
        h2Path = props.getProperty("db.h2.path", "./data/srms_db").trim();
    }

    public static synchronized Connection getConnection() throws SQLException {
        if ("mysql".equalsIgnoreCase(dbType) && !usingH2Fallback) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String mysqlUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                        "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                Connection conn = DriverManager.getConnection(mysqlUrl, username, password);
                activeDbInfo = "MySQL Database (" + host + ":" + port + "/" + dbName + ")";
                usingH2Fallback = false;
                return conn;
            } catch (Exception e) {
                if (fallbackH2) {
                    System.out.println("[DatabaseConnection] MySQL connection failed (" + e.getMessage() + "). Switching to Embedded H2 Database (MySQL compatibility mode).");
                    usingH2Fallback = true;
                } else {
                    throw new SQLException("Failed to connect to MySQL server at " + host + ":" + port + ": " + e.getMessage(), e);
                }
            }
        }

        // H2 Embedded Database
        try {
            Class.forName("org.h2.Driver");
            File dataDir = new File(h2Path).getParentFile();
            if (dataDir != null && !dataDir.exists()) {
                dataDir.mkdirs();
            }
            String h2Url = "jdbc:h2:" + h2Path + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
            Connection conn = DriverManager.getConnection(h2Url, "sa", "");
            activeDbInfo = "Embedded Database (Local Storage: " + h2Path + ")";
            usingH2Fallback = true;
            return conn;
        } catch (Exception e) {
            throw new SQLException("Failed to initialize Embedded database: " + e.getMessage(), e);
        }
    }

    public static boolean isUsingH2Fallback() {
        return usingH2Fallback;
    }

    public static String getActiveDbInfo() {
        return activeDbInfo.isEmpty() ? "Not connected" : activeDbInfo;
    }

    public static void setConfig(String newHost, int newPort, String newDbName, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        dbName = newDbName;
        username = newUser;
        password = newPass;
        dbType = "mysql";
        usingH2Fallback = false;
    }

    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}
