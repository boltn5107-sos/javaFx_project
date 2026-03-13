package com.avec.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	
	private static final String URL = "jdbc:mysql://localhost/gestion_avec";
	private static final String USER = "root";
	private static final String PASSWORD = "";
	
	private static Connection connexion = null;
	
	public Connection getConnection() {
		
		if(connexion == null) {
			try {
				
				connexion = DriverManager.getConnection(URL,USER,PASSWORD);
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return connexion;
	}
	
	public void closeConnection() {
		
		if(connexion != null) {
			
			try {
			connexion.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
