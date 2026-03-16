package com.avec.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.model.AgentTerrain;
import com.avec.model.Utilisateur;

public class AgentTerrainDao {
	
	private UtilisateurDao utilisateurDao;
	
	public AgentTerrainDao() {
		this.utilisateurDao = new UtilisateurDao();
	}
	
	// Créer agentTerrain
	
	public boolean enregistrer(AgentTerrain agentTerrain) {
		
		// D'abord sauvegarder dans l'utilisateur
		
		if(!utilisateurDao.ajouter(agentTerrain)) {
			
			return false;
		}
		
		// Ensuite dans agentTerrain
		
		String sql = "INSERT INTO agentterrain(id) VALUES (?)";
		
		try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setLong(1, agentTerrain.getId());
			
			return ps.executeUpdate() > 0;
		}catch(SQLException e) {
			
			System.err.println("Erreur lors de la sauvegarde de l'agent terrain: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	 // Chercher par id
    public AgentTerrain chercherId(Long id) {
        String sql = "SELECT * FROM agentterrain WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAgentTerrain(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lister tout
    public List<AgentTerrain> lister() {
        List<AgentTerrain> agents = new ArrayList<>();
        String sql = "SELECT * FROM agentterrain";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                agents.add(mapResultSetToAgentTerrain(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agents;
    }
    
    // Mettre à jour AT
    public boolean modifier(AgentTerrain agentTerrain) {
        // Mettre à jour Utilisateur seulement car AgentTerrain n'a pas d'autres champs
        return utilisateurDao.modifier(agentTerrain);
    }
    
    // Supprimer
    public boolean supprimer(Long id) {
        // Supprimer d'abord de AgentTerrain
        String sql = "DELETE FROM agentterrain WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            
            // Puis supprimer de Utilisateur
            if (deleted) {
                return utilisateurDao.spprimer(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // COUNT
    public int compter() {
        String sql = "SELECT COUNT(*) FROM agentterrain";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Mapping ResultSet -> AgentTerrain
    private AgentTerrain mapResultSetToAgentTerrain(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        
        // Récupérer les informations de base depuis Utilisateur
        Utilisateur utilisateur = utilisateurDao.chercherId(id);
        if (utilisateur == null) {
            return null;
        }
        
        return new AgentTerrain(utilisateur);
    }
}


