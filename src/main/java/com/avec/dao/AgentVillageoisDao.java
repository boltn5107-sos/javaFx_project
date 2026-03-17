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
    
    private AgentTerrainDao agentTerrainDao;
    
    public AgentVillageoisDao() {
        this.agentTerrainDao = new AgentTerrainDao();
    }
    
    // ENREGISTRER (Créer un nouvel agent villageois)
    public boolean enregistrer(AgentVillageois agent) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // 1. Insérer dans Utilisateur
            String sqlUser = "INSERT INTO Utilisateur (nom, prenom, email, motDePasse, telephone) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, agent.getNom());
            pstmt.setString(2, agent.getPrenom());
            pstmt.setString(3, agent.getEmail());
            pstmt.setString(4, agent.getMotDePasse());
            pstmt.setString(5, agent.getTelephone());
            
            int rows = pstmt.executeUpdate();
            if (rows == 0) return false;
            
            // Récupérer l'ID généré
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                agent.setId(rs.getLong(1));
            }
            rs.close();
            pstmt.close();
            
            // 2. Insérer dans AgentVillageois
            String sqlAgent = "INSERT INTO AgentVillageois (id, agentTerrain_id) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sqlAgent);
            pstmt.setLong(1, agent.getId());
            pstmt.setLong(2, agent.getAgentTerrain().getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur dans enregistrer(): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // CHERCHER PAR ID
    public AgentVillageois chercherId(Long id) {
        if (id == null) return null;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT av.id, av.agentTerrain_id, " +
                         "u.nom, u.prenom, u.email, u.telephone, u.motDePasse " +
                         "FROM AgentVillageois av " +
                         "JOIN Utilisateur u ON av.id = u.id " +
                         "WHERE av.id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getLong("id"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setTelephone(rs.getString("telephone"));
                utilisateur.setMotDePasse(rs.getString("motDePasse"));
                
                AgentVillageois agent = new AgentVillageois(utilisateur);
                
                Long agentTerrainId = rs.getLong("agentTerrain_id");
                if (agentTerrainId != null && agentTerrainId > 0) {
                    // Utiliser le DAO d'agent terrain pour chercher
                    AgentTerrain agentTerrain = agentTerrainDao.chercherId(agentTerrainId);
                    agent.setAgentTerrain(agentTerrain);
                }
                
                return agent;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur dans chercherId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    // LISTER tous les agents villageois
    public List<AgentVillageois> lister() {
        List<AgentVillageois> agents = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Récupérer d'abord tous les IDs
            String sqlIds = "SELECT id FROM AgentVillageois";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlIds);
            
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }
            rs.close();
            stmt.close();
            
            // Puis pour chaque ID, chercher l'agent complet
            for (Long id : ids) {
                AgentVillageois agent = chercherId(id);
                if (agent != null) {
                    agents.add(agent);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur dans lister(): " + e.getMessage());
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
    
    // CHERCHER PAR AGENT TERRAIN
    public List<AgentVillageois> chercherParAt(Long agentTerrainId) {
        List<AgentVillageois> agents = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id FROM AgentVillageois WHERE agentTerrain_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, agentTerrainId);
            rs = pstmt.executeQuery();
            
            List<Long> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }
            rs.close();
            pstmt.close();
            
            for (Long id : ids) {
                AgentVillageois agent = chercherId(id);
                if (agent != null) {
                    agents.add(agent);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur dans chercherParAt(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return agents;
    }
    
    // MODIFIER
    public boolean modifier(AgentVillageois agent) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // 1. Mettre à jour Utilisateur
            String sqlUser = "UPDATE Utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sqlUser);
            pstmt.setString(1, agent.getNom());
            pstmt.setString(2, agent.getPrenom());
            pstmt.setString(3, agent.getEmail());
            pstmt.setString(4, agent.getTelephone());
            pstmt.setLong(5, agent.getId());
            
            boolean userUpdated = pstmt.executeUpdate() > 0;
            pstmt.close();
            
            // 2. Mettre à jour AgentVillageois
            String sqlAgent = "UPDATE AgentVillageois SET agentTerrain_id = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sqlAgent);
            pstmt.setLong(1, agent.getAgentTerrain().getId());
            pstmt.setLong(2, agent.getId());
            
            return pstmt.executeUpdate() > 0 && userUpdated;
            
        } catch (SQLException e) {
            System.err.println("Erreur dans modifier(): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // SUPPRIMER
    public boolean supprimer(Long id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // 1. Supprimer d'AgentVillageois
            String sqlAgent = "DELETE FROM AgentVillageois WHERE id = ?";
            pstmt = conn.prepareStatement(sqlAgent);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            
            // 2. Supprimer d'Utilisateur
            String sqlUser = "DELETE FROM Utilisateur WHERE id = ?";
            pstmt = conn.prepareStatement(sqlUser);
            pstmt.setLong(1, id);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur dans supprimer(): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // COMPTER
    public int compter() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM AgentVillageois";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur dans compter(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return 0;
    }
}