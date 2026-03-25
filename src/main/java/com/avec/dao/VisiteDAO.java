package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.AgentTerrain;
import com.avec.model.AgentVillageois;
import com.avec.model.Avec;
import com.avec.model.Visite;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisiteDAO {
    
    private AvecDAO avecDAO;
    private AgentVillageoisDao agentVillageoisDAO;
    private AgentTerrainDao agentTerrainDAO;
    
    public VisiteDAO() {
        this.avecDAO = new AvecDAO();
        this.agentVillageoisDAO = new AgentVillageoisDao();
        this.agentTerrainDAO = new AgentTerrainDao();
    }
    
    /**
     * Enregistre une nouvelle visite
     */
    public boolean enregistrer(Visite visite) {
        String sql = "INSERT INTO visite (date, module, observations, avec_id, agentVillageois_id, superviseurPresent_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(visite.getDate()));
            ps.setString(2, visite.getModule());
            ps.setString(3, visite.getObservations());
            ps.setLong(4, visite.getAvecId());
            ps.setLong(5, visite.getAgentVillageoisId());
            
            if (visite.getSuperviseurPresentId() != null) {
                ps.setLong(6, visite.getSuperviseurPresentId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        visite.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la visite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Met à jour une visite existante
     */
    public boolean update(Visite visite) {
        String sql = "UPDATE visite SET date = ?, module = ?, observations = ?, " +
                     "avec_id = ?, agentVillageois_id = ?, superviseurPresent_id = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(visite.getDate()));
            ps.setString(2, visite.getModule());
            ps.setString(3, visite.getObservations());
            ps.setLong(4, visite.getAvecId());
            ps.setLong(5, visite.getAgentVillageoisId());
            
            if (visite.getSuperviseurPresentId() != null) {
                ps.setLong(6, visite.getSuperviseurPresentId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            
            ps.setLong(7, visite.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la visite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cherche une visite par son ID
     */
    public Visite chercherId(Long id) throws SQLException {
        String sql = "SELECT * FROM visite WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVisite(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Liste toutes les visites
     */
    public List<Visite> lister() throws SQLException {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM visite ORDER BY date DESC, id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                visites.add(mapResultSetToVisite(rs));
            }
        }
        return visites;
    }
    
    /**
     * Liste les visites par AVEC
     */
    public List<Visite> listerParAvecId(Long avecId) throws SQLException {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM visite WHERE avec_id = ? ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, avecId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visites.add(mapResultSetToVisite(rs));
                }
            }
        }
        return visites;
    }
    
    /**
     * Liste les visites par agent villageois
     */
    public List<Visite> listerParAgentVillageoisId(Long agentId) throws SQLException {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM visite WHERE agentVillageois_id = ? ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, agentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visites.add(mapResultSetToVisite(rs));
                }
            }
        }
        return visites;
    }
    
    /**
     * Liste les visites par superviseur (agent terrain)
     */
    public List<Visite> listerParSuperviseurId(Long superviseurId) throws SQLException {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM visite WHERE superviseurPresent_id = ? ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, superviseurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visites.add(mapResultSetToVisite(rs));
                }
            }
        }
        return visites;
    }
    
    /**
     * Liste les visites par module
     */
    public List<Visite> listerParModule(String module) throws SQLException {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM visite WHERE module = ? ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, module);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visites.add(mapResultSetToVisite(rs));
                }
            }
        }
        return visites;
    }
    
    /**
     * Liste les visites par période
     */
    public List<Visite> listerParPeriode(LocalDateTime debut, LocalDateTime fin) throws SQLException {
        List<Visite> visites = new ArrayList<>();
        String sql = "SELECT * FROM visite WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visites.add(mapResultSetToVisite(rs));
                }
            }
        }
        return visites;
    }
    
    /**
     * Compte le nombre de visites pour une AVEC
     */
    public int countByAvecId(Long avecId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visite WHERE avec_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, avecId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Compte le nombre de visites pour un agent villageois
     */
    public int countByAgentVillageoisId(Long agentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visite WHERE agentVillageois_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, agentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Compte le nombre de visites pour un superviseur (agent terrain)
     */
    public int countBySuperviseurId(Long superviseurId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visite WHERE superviseurPresent_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, superviseurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Supprime une visite
     */
    public boolean supprimer(Long id) throws SQLException {
        String sql = "DELETE FROM visite WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map un ResultSet vers un objet Visite
     */
    private Visite mapResultSetToVisite(ResultSet rs) throws SQLException {
        Visite visite = new Visite();
        
        visite.setId(rs.getLong("id"));
        
        Timestamp date = rs.getTimestamp("date");
        if (date != null) {
            visite.setDate(date.toLocalDateTime());
        }
        
        visite.setModule(rs.getString("module"));
        visite.setObservations(rs.getString("observations"));
        
        Long avecId = rs.getLong("avec_id");
        if (!rs.wasNull()) {
            visite.setAvecId(avecId);
            Avec avec = avecDAO.findById(avecId);
            visite.setAvec(avec);
        }
        
        Long agentId = rs.getLong("agentVillageois_id");
        if (!rs.wasNull()) {
            visite.setAgentVillageoisId(agentId);
            AgentVillageois agent = agentVillageoisDAO.chercherId(agentId);
            visite.setAgentVillageois(agent);
        }
        
        Long superviseurId = rs.getLong("superviseurPresent_id");
        if (!rs.wasNull()) {
            visite.setSuperviseurPresentId(superviseurId);
            AgentTerrain superviseur = agentTerrainDAO.chercherId(superviseurId);
            visite.setSuperviseurPresent(superviseur);
        }
        
        return visite;
    }
}