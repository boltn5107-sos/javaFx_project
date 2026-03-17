package com.avec.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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

		if (!utilisateurDao.ajouter(agentTerrain)) {

			return false;
		}

		// Ensuite dans agentTerrain

		String sql = "INSERT INTO agentterrain(id) VALUES (?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, agentTerrain.getId());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {

			System.err.println("Erreur lors de la sauvegarde de l'agent terrain: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	// Chercher par id
	public AgentTerrain chercherId(Long id) {

		AgentTerrain agent = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String sql = "SELECT * FROM agentterrain WHERE id = ?";
			stmt = conn.prepareStatement(sql);

			stmt.setLong(1, id);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				return null;
			}

			// Récupérer l'ID (on sait déjà que c'est id)
			rs.close();
			stmt.close();

			// 2. Récupérer l'utilisateur correspondant
			Utilisateur utilisateur = utilisateurDao.chercherId(id);
			if (utilisateur != null) {
				agent = new AgentTerrain(utilisateur);
			}

		} catch (SQLException e) {
			System.err.println("Erreur chercherId: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return agent;
	}

	// Lister tout
	public List<AgentTerrain> lister() {
		List<AgentTerrain> agents = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String sql = "SELECT * FROM agentterrain";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			// Récupérer tous les résultats d'abord
			List<Long> ids = new ArrayList<>();
			while (rs.next()) {
				ids.add(rs.getLong("id"));
			}

			// Fermer ResultSet et Statement après avoir récupéré les IDs
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
				}

			// Maintenant charger chaque agent individuellement
			for (Long id : ids) {
				AgentTerrain agent = chercherId(id);
				if (agent != null) {
					agents.add(agent);
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur lors du lister: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Ne pas fermer la connexion ici

		}
		return agents;
	}
	
	 // Version alternative plus simple pour lister
    public List<AgentTerrain> listerSimple() {
        List<AgentTerrain> agents = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT a.*, u.nom, u.prenom, u.email, u.telephone " +
                         "FROM AgentTerrain a " +
                         "JOIN Utilisateur u ON a.id = u.id";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getLong("id"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setTelephone(rs.getString("telephone"));
                // Ne pas récupérer le mot de passe ici pour des raisons de sécurité
                
                AgentTerrain agent = new AgentTerrain(utilisateur);
                agents.add(agent);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur listerSimple: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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




    public AgentTerrain findAgentTerrainById(Long id) throws SQLException {
        String sql = "SELECT * FROM agents_terrain WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AgentTerrain agent = new AgentTerrain();

                    // Propriétés de base
                    agent.setId(rs.getLong("id"));
                    agent.setNom(rs.getString("nom"));
                    agent.setEmail(rs.getString("email"));
                    agent.setMotDePasse(rs.getString("mot_de_passe"));
                    agent.setTelephone(rs.getString("telephone"));
                   // agent.setActif(rs.getBoolean("actif"));

//                    Date dateCreation = rs.getDate("date_creation");
//                    if (dateCreation != null) {
//                        agent.setDateCreation(dateCreation.toLocalDate());
//                    }
//
//                    Date derniereConnexion = rs.getDate("derniere_connexion");
//                    if (derniereConnexion != null) {
//                        agent.setDerniereConnexion(derniereConnexion.toLocalDate());
//                    }

                    // Propriétés spécifiques
                    //agent.setZoneIntervention(rs.getString("zone_intervention"));

                    return agent;
                }
            }
        }
        return null;
    }

}
