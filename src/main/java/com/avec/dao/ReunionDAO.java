package com.avec.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.enums.StatutReunion;
import com.avec.enums.TypeReunion;
import com.avec.model.Reunion;

public class ReunionDAO {

	 /**
     * Enregistre une nouvelle réunion - VERSION CORRIGÉE
     */
    public boolean enregistrer(Reunion reunion) {
        // ✅ Requête avec le bon nombre de paramètres (7 colonnes)
        String sql = "INSERT INTO reunion (date, type, statut, cycle_id, " +
                     "soldeFondCreditAvant, soldesFondsCreditApres, soldeCaisseSolidaritesApres) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        System.out.println(">>> DAO: Enregistrement réunion");
        System.out.println(">>> SQL: " + sql);
        System.out.println(">>> Date: " + reunion.getDate());
        System.out.println(">>> Type: " + reunion.getType());
        System.out.println(">>> Statut: " + reunion.getStatut());
        System.out.println(">>> CycleId: " + reunion.getCycleId());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Paramètre 1: date
            ps.setDate(1, Date.valueOf(reunion.getDate()));
            
            // Paramètre 2: type
            ps.setString(2, reunion.getType().name());
            
            // Paramètre 3: statut
            ps.setString(3, reunion.getStatut().name());
            
            // Paramètre 4: cycle_id (peut être null)
            if (reunion.getCycleId() != null) {
                ps.setLong(4, reunion.getCycleId());
            } else {
                ps.setNull(4, Types.BIGINT);
            }
            
            // Paramètre 5: soldeFondCreditAvant
            ps.setBigDecimal(5, reunion.getSoldeFondCreditAvant() != null ? 
                             reunion.getSoldeFondCreditAvant() : java.math.BigDecimal.ZERO);
            
            // Paramètre 6: soldesFondsCreditApres
            ps.setBigDecimal(6, reunion.getSoldesFondsCreditApres() != null ? 
                             reunion.getSoldesFondsCreditApres() : java.math.BigDecimal.ZERO);
            
            // Paramètre 7: soldeCaisseSolidaritesApres
            ps.setBigDecimal(7, reunion.getSoldeCaisseSolidaritesApres() != null ? 
                             reunion.getSoldeCaisseSolidaritesApres() : java.math.BigDecimal.ZERO);

            int affectedRows = ps.executeUpdate();
            System.out.println(">>> Affected rows: " + affectedRows);
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reunion.setId(generatedKeys.getLong(1));
                        System.out.println(">>> ID généré: " + reunion.getId());
                    }
                }
                return true;
            }
            
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la réunion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Met à jour une réunion existante
     */
    public boolean update(Reunion reunion) {
        String sql = "UPDATE reunion SET date = ?, type = ?, statut = ?, cycle_id = ?, " +
                     "soldeFondCreditAvant = ?, soldesFondsCreditApres = ?, " +
                     "soldeCaisseSolidaritesApres = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(reunion.getDate()));
            ps.setString(2, reunion.getType().name());
            ps.setString(3, reunion.getStatut().name());
            
            if (reunion.getCycleId() != null) {
                ps.setLong(4, reunion.getCycleId());
            } else {
                ps.setNull(4, Types.BIGINT);
            }
            
            ps.setBigDecimal(5, reunion.getSoldeFondCreditAvant());
            ps.setBigDecimal(6, reunion.getSoldesFondsCreditApres());
            ps.setBigDecimal(7, reunion.getSoldeCaisseSolidaritesApres());
            ps.setLong(8, reunion.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cherche une réunion par son ID
     */
    public Reunion chercherId(Long id) throws SQLException {
        String sql = "SELECT * FROM reunion WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReunion(rs);
                }
            }
        }
        return null;
    }

    /**
     * Liste toutes les réunions
     */
    public List<Reunion> lister() throws SQLException {
        List<Reunion> reunions = new ArrayList<>();
        String sql = "SELECT * FROM reunion ORDER BY date DESC, id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reunions.add(mapResultSetToReunion(rs));
            }
        }
        return reunions;
    }
    
    /**
     * Récupère toutes les réunions d'une AVEC
     * @param avecId L'ID de l'AVEC
     * @return Liste des réunions de l'AVEC
     */
    public List<Reunion> findByAvecId(Long avecId) throws SQLException {
        System.out.println(">>> [ReunionDAO] findByAvecId called with avecId: " + avecId);
        List<Reunion> reunions = new ArrayList<>();
        
        String sql = "SELECT r.* FROM reunion r " +
                     "INNER JOIN cycle c ON r.cycle_id = c.id " +
                     "WHERE c.avec_id = ? " +
                     "ORDER BY r.date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, avecId);
            System.out.println(">>> [ReunionDAO] Executing query...");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reunion r = mapResultSetToReunion(rs);
                    System.out.println(">>> [ReunionDAO] Found reunion: id=" + r.getId() + ", type=" + r.getType());
                    reunions.add(r);
                }
            }
        }
        System.out.println(">>> [ReunionDAO] Total found: " + reunions.size());
        return reunions;
    }
    

    /**
     * Liste les réunions par cycle
     */
    public List<Reunion> listerParCycleId(Long cycleId) throws SQLException {
        List<Reunion> reunions = new ArrayList<>();
        String sql = "SELECT * FROM reunion WHERE cycle_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cycleId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reunions.add(mapResultSetToReunion(rs));
                }
            }
        }
        return reunions;
    }

    /**
     * Liste les réunions par type
     */
    public List<Reunion> listerParType(TypeReunion type) throws SQLException {
        List<Reunion> reunions = new ArrayList<>();
        String sql = "SELECT * FROM reunion WHERE type = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reunions.add(mapResultSetToReunion(rs));
                }
            }
        }
        return reunions;
    }

    /**
     * Liste les réunions par statut
     */
    public List<Reunion> listerParStatut(StatutReunion statut) throws SQLException {
        List<Reunion> reunions = new ArrayList<>();
        String sql = "SELECT * FROM reunion WHERE statut = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reunions.add(mapResultSetToReunion(rs));
                }
            }
        }
        return reunions;
    }

    /**
     * Trouve la réunion en cours pour un cycle
     */
    public Reunion findReunionEnCours(Long cycleId) throws SQLException {
        String sql = "SELECT * FROM reunion WHERE cycle_id = ? AND statut = 'EN_COURS' LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cycleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReunion(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Trouve la réunion en cours pour une AVEC directement
     */
    public Reunion findLatestReunionByAvecId(Long avecId) throws SQLException {
        String sql = "SELECT r.* FROM reunion r " +
                     "LEFT JOIN cycle c ON r.cycle_id = c.id " +
                     "WHERE c.avec_id = ? OR r.cycle_id IS NULL " +
                     "ORDER BY r.id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReunion(rs);
                }
            }
        }
        return null;
    }
    
    public Reunion findReunionEnCoursByAvecId(Long avecId) throws SQLException {
        String sql = "SELECT DISTINCT r.* FROM reunion r " +
                    "LEFT JOIN cycle c ON r.cycle_id = c.id " +
                    "WHERE (c.avec_id = ? OR r.cycle_id IS NULL) AND r.statut = 'EN_COURS' " +
                    "ORDER BY r.date DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReunion(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Trouve toutes les réunions pour une AVEC
     */
//    public List<Reunion> findByAvecId(Long avecId) throws SQLException {
//        List<Reunion> reunions = new ArrayList<>();
//        String sql = "SELECT r.* FROM reunion r " +
//                     "LEFT JOIN cycle c ON r.cycle_id = c.id " +
//                     "WHERE c.avec_id = ? OR r.cycle_id IS NULL " +
//                     "ORDER BY r.date DESC";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setLong(1, avecId);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                while (rs.next()) {
//                    reunions.add(mapResultSetToReunion(rs));
//                }
//            }
//        }
//        return reunions;
//    }

    /**
     * Trouve la prochaine réunion planifiée pour un cycle
     */
    public Reunion findProchaineReunion(Long cycleId) throws SQLException {
        String sql = "SELECT * FROM reunion WHERE cycle_id = ? AND statut = 'PLANIFIEE' " +
                     "AND date >= CURDATE() ORDER BY date ASC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cycleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReunion(rs);
                }
            }
        }
        return null;
    }

    /**
     * Met à jour le statut d'une réunion
     */
    public boolean updateStatut(Long reunionId, StatutReunion statut) throws SQLException {
        String sql = "UPDATE reunion SET statut = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            stmt.setLong(2, reunionId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour les soldes après une réunion
     */
    public boolean updateSoldes(Long reunionId, BigDecimal soldeFondCreditApres, 
                                 BigDecimal soldeCaisseSolidaritesApres) throws SQLException {
        String sql = "UPDATE reunion SET soldes_fonds_credit_apres = ?, " +
                     "solde_caisse_solidarites_apres = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, soldeFondCreditApres);
            stmt.setBigDecimal(2, soldeCaisseSolidaritesApres);
            stmt.setLong(3, reunionId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une réunion
     */
    public boolean supprimer(Long id) throws SQLException {
        String sql = "DELETE FROM reunion WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Compte le nombre total de réunions
     */
    public int compter() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reunion";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Compte le nombre de réunions par cycle
     */
    public int compterParCycleId(Long cycleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reunion WHERE cycle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cycleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Map un ResultSet vers un objet Reunion
     */
    private Reunion mapResultSetToReunion(ResultSet rs) throws SQLException {
        Reunion reunion = new Reunion();

        reunion.setId(rs.getLong("id"));
        
        Date date = rs.getDate("date");
        if (date != null) {
            reunion.setDate(date.toLocalDate());
        }
        
        reunion.setType(TypeReunion.valueOf(rs.getString("type")));
        
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            reunion.setStatut(StatutReunion.valueOf(statutStr));
        }
        
        long cycleId = rs.getLong("cycle_id");
        if (!rs.wasNull()) {
            reunion.setCycleId(cycleId);
        }
        
        reunion.setSoldeFondCreditAvant(rs.getBigDecimal("soldeFondCreditAvant"));
        reunion.setSoldesFondsCreditApres(rs.getBigDecimal("soldesFondsCreditApres"));
        reunion.setSoldeCaisseSolidaritesApres(rs.getBigDecimal("soldeCaisseSolidaritesApres"));

        return reunion;
    }
}