package com.avec.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/avec_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données établie avec succès!");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL non trouvé!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données!");
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}