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
import com.avec.model.AgentVillageois;
import com.avec.model.Utilisateur;

public class AgentVillageoisDao {

	private UtilisateurDao utilisateurDao;
	private AgentTerrainDao agentTerrainDao;

	public AgentVillageoisDao() {
		this.utilisateurDao = new UtilisateurDao();
		this.agentTerrainDao = new AgentTerrainDao();
	}

	// Créé agent villageois

	public boolean enregistrer(AgentVillageois agentVillageois) {
		// D'baord sauvegarder dans l'utilisateur
		if (!utilisateurDao.ajouter(agentVillageois)) {
			return false;
		}

		// Ensuite dans AgentVillageois

		String sql = "INSERT INTO agentvillageois(id, agentTerrain_id) VALUES (?,?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, agentVillageois.getId());
			ps.setLong(2, agentVillageois.getAgentTerrain().getId());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Erreur lors de la sauvegarde de l'agent villageois: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	// Chercher par l'id

	public AgentVillageois chercherId(Long id) {

		String sql = "SELECT * FROM agentvillageois WHERE id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				return mapResultsetToAgentVillageois(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Lire tout

	public List<AgentVillageois> lister() {

		List<AgentVillageois> agents = new ArrayList<>();

		String sql = "SELECT * FROM agentvillageois";

		try (Connection con = DBConnection.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				agents.add(mapResultsetToAgentVillageois(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return agents;

	}

	// Chercher par en fonction de l'AT

	public List<AgentVillageois> chercherParAt(Long agentTerrain_id) {

		List<AgentVillageois> agents = new ArrayList<>();
		String sql = "SELECT * FROM agentvillageois WHERE agentTerrain_id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, agentTerrain_id);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				agents.add(mapResultsetToAgentVillageois(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return agents;
	}

	// Modifier un AV

	public boolean modifier(AgentVillageois agentVillageois) {
		// Mettre à jour d'abord table utilisateur
		if (!utilisateurDao.modifier(agentVillageois)) {
			return false;
		}

		// Mettre à jour AV

		String sql = "UDPDATE agentvillageois SET agentTerrain_id =? WHERE id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, agentVillageois.getAgentTerrain().getId());
			ps.setLong(2, agentVillageois.getId());
			
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	
	// Supprimer un AV
    public boolean supprimer(Long id) {
        // Supprimer d'abord de AgentVillageois
        String sql = "DELETE FROM agentvillageois WHERE id = ?";
        
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
    
    // COMPTER
    public int compter() {
        String sql = "SELECT COUNT(*) FROM agentvillageois";
        
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
    

	// Mapping ResultSet -> agentVillageois
	private AgentVillageois mapResultsetToAgentVillageois(ResultSet rs) throws SQLException {

		Long id = rs.getLong("id");

		// Récupérer les informations de base depuis utilisateur
		Utilisateur utilisateur = utilisateurDao.chercherId(id);

		if (utilisateur == null) {
			return null;
		}

		AgentVillageois agent = new AgentVillageois(utilisateur);

		// Récupérer l'agent de terrain associé

		Long agentTerrainId = rs.getLong("agentTerrain_id");

		if (agentTerrainId != null) {

			AgentTerrain agentTerrain = agentTerrainDao.chercherId(agentTerrainId);

			agent.setAgentTerrain(agentTerrain);
		}

		return agent;
	}

}
