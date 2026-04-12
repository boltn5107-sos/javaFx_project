package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Membre;
import com.avec.model.Presence;
import com.avec.model.Reunion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PresenceDAO {

    private Connection connection;
    private MembreDAO membreDAO;
    private ReunionDAO reunionDAO;

    public PresenceDAO() {
        try {
            this.connection = DBConnection.getConnection();
            this.membreDAO = new MembreDAO();
            this.reunionDAO = new ReunionDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enregistre une présence
     */
    public Presence insert(Presence presence) throws SQLException {
        String sql = "INSERT INTO presence (membre_id, reunion_id, est_present, est_retard, heure_arrivee, motif_absence) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE est_present = ?, est_retard = ?, heure_arrivee = ?, motif_absence = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, presence.getMembreId());
            stmt.setLong(2, presence.getReunionId());
            stmt.setBoolean(3, presence.getEstPresent());
            stmt.setBoolean(4, presence.getEstRetard());
            
            if (presence.getHeureArrivee() != null) {
                stmt.setTime(5, Time.valueOf(presence.getHeureArrivee()));
            } else {
                stmt.setNull(5, Types.TIME);
            }
            
            stmt.setString(6, presence.getMotifAbsence());
            
            // Pour UPDATE
            stmt.setBoolean(7, presence.getEstPresent());
            stmt.setBoolean(8, presence.getEstRetard());
            
            if (presence.getHeureArrivee() != null) {
                stmt.setTime(9, Time.valueOf(presence.getHeureArrivee()));
            } else {
                stmt.setNull(9, Types.TIME);
            }
            
            stmt.setString(10, presence.getMotifAbsence());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'enregistrement de la présence a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    presence.setId(generatedKeys.getLong(1));
                }
            }
        }
        return presence;
    }

    /**
     * Récupère toutes les présences d'une réunion
     */
    public List<Presence> findByReunionId(Long reunionId) throws SQLException {
        List<Presence> presences = new ArrayList<>();
        String sql = "SELECT * FROM presence WHERE reunion_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reunionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    presences.add(mapResultSetToPresence(rs));
                }
            }
        }
        return presences;
    }

    /**
     * Récupère la présence d'un membre pour une réunion spécifique
     */
    public Presence findByMembreAndReunion(Long membreId, Long reunionId) throws SQLException {
        String sql = "SELECT * FROM presence WHERE membre_id = ? AND reunion_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, membreId);
            stmt.setLong(2, reunionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPresence(rs);
                }
            }
        }
        return null;
    }

    /**
     * Récupère toutes les présences d'un membre
     */
    public List<Presence> findByMembreId(Long membreId) throws SQLException {
        List<Presence> presences = new ArrayList<>();
        String sql = "SELECT * FROM presence WHERE membre_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    presences.add(mapResultSetToPresence(rs));
                }
            }
        }
        return presences;
    }

    /**
     * Compte le nombre d'absences pour un membre
     */
    public int countAbsencesByMembre(Long membreId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presence WHERE membre_id = ? AND est_present = FALSE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Compte le nombre de retards pour un membre
     */
    public int countRetardsByMembre(Long membreId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presence WHERE membre_id = ? AND est_retard = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Map ResultSet vers Presence
     */
    private Presence mapResultSetToPresence(ResultSet rs) throws SQLException {
        Presence presence = new Presence();
        
        presence.setId(rs.getLong("id"));
        presence.setMembreId(rs.getLong("membre_id"));
        presence.setReunionId(rs.getLong("reunion_id"));
        presence.setEstPresent(rs.getBoolean("est_present"));
        presence.setEstRetard(rs.getBoolean("est_retard"));
        
        Time heureArrivee = rs.getTime("heure_arrivee");
        if (heureArrivee != null) {
            presence.setHeureArrivee(heureArrivee.toLocalTime());
        }
        
        presence.setMotifAbsence(rs.getString("motif_absence"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            presence.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            presence.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Charger les objets associés
        Membre membre = membreDAO.findById(presence.getMembreId());
        presence.setMembre(membre);
        
        Reunion reunion = reunionDAO.chercherId(presence.getReunionId());
        presence.setReunion(reunion);
        
        return presence;
    }
}