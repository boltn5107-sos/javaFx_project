package com.avec.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/gestion_avec";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	private static Connection connection = null;

	public static Connection getConnection() {

		try {

			// Si la connexion est null ou fermée, en créer une nouvelle
			if (connection == null || connection.isClosed()) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(URL, USER, PASSWORD);
				System.out.println("Connexion à la base de données établie avec succès!");
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Driver MySQL non trouvé!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Erreur de connexion à la base de données!");
			e.printStackTrace();
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
	
	  // Cette méthode ne fait rien maintenant - on garde la connexion ouverte
    public static void releaseConnection(Connection conn) {
        // Ne rien faire - on garde la même connexion
    }
}