package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.DecaissementPret;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des décaissements de prêts
 */
public class DecaissementPretDAO {

    /**
     * Insère un nouveau décaissement
     */
    public DecaissementPret insert(DecaissementPret decaissement) throws SQLException {
        String sql = "INSERT INTO decaissements_prets (numero_decaissement, montant, date_decaissement, " +
                "mode_paiement, observations, pret_id, reunion_id, encaisse_par_id, approuve_par_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, decaissement.getNumeroDecaissement());
            stmt.setBigDecimal(2, decaissement.getMontant());
            stmt.setTimestamp(3, Timestamp.valueOf(decaissement.getDateDecaissement()));
            stmt.setString(4, decaissement.getModePaiement());
            stmt.setString(5, decaissement.getObservations());
            stmt.setLong(6, decaissement.getPretId());
            stmt.setLong(7, decaissement.getReunionId());

            if (decaissement.getEncaisseParId() != null) {
                stmt.setLong(8, decaissement.getEncaisseParId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            if (decaissement.getApprouveParId() != null) {
                stmt.setLong(9, decaissement.getApprouveParId());
            } else {
                stmt.setNull(9, Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du décaissement a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    decaissement.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création du décaissement a échoué, aucun ID obtenu");
                }
            }
        }
        return decaissement;
    }

    /**
     * Met à jour un décaissement existant
     */
    public boolean update(DecaissementPret decaissement) throws SQLException {
        String sql = "UPDATE decaissements_prets SET montant = ?, date_decaissement = ?, " +
                "mode_paiement = ?, observations = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, decaissement.getMontant());
            stmt.setTimestamp(2, Timestamp.valueOf(decaissement.getDateDecaissement()));
            stmt.setString(3, decaissement.getModePaiement());
            stmt.setString(4, decaissement.getObservations());
            stmt.setLong(5, decaissement.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime un décaissement
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM decaissements_prets WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve un décaissement par son ID
     */
    public DecaissementPret findById(long id) throws SQLException {
        String sql = "SELECT * FROM decaissements_prets WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDecaissement(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve les décaissements par prêt
     */
    public List<DecaissementPret> findByPretId(long pretId) throws SQLException {
        List<DecaissementPret> decaissements = new ArrayList<>();
        String sql = "SELECT * FROM decaissements_prets WHERE pret_id = ? ORDER BY date_decaissement DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pretId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    decaissements.add(mapResultSetToDecaissement(rs));
                }
            }
        }
        return decaissements;
    }

    /**
     * Trouve les décaissements par réunion
     */
    public List<DecaissementPret> findByReunionId(long reunionId) throws SQLException {
        List<DecaissementPret> decaissements = new ArrayList<>();
        String sql = "SELECT * FROM decaissements_prets WHERE reunion_id = ? ORDER BY date_decaissement DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reunionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    decaissements.add(mapResultSetToDecaissement(rs));
                }
            }
        }
        return decaissements;
    }

    /**
     * Trouve les décaissements par membre (encaisseur)
     */
    public List<DecaissementPret> findByEncaisseurId(long membreId) throws SQLException {
        List<DecaissementPret> decaissements = new ArrayList<>();
        String sql = "SELECT * FROM decaissements_prets WHERE encaisse_par_id = ? ORDER BY date_decaissement DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, membreId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    decaissements.add(mapResultSetToDecaissement(rs));
                }
            }
        }
        return decaissements;
    }

    /**
     * Trouve les décaissements par période
     */
    public List<DecaissementPret> findByPeriode(LocalDateTime debut, LocalDateTime fin) throws SQLException {
        List<DecaissementPret> decaissements = new ArrayList<>();
        String sql = "SELECT * FROM decaissements_prets WHERE date_decaissement BETWEEN ? AND ? ORDER BY date_decaissement DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    decaissements.add(mapResultSetToDecaissement(rs));
                }
            }
        }
        return decaissements;
    }

    /**
     * Calcule le total des décaissements pour un prêt
     */
    public BigDecimal sumMontantByPretId(long pretId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant), 0) FROM decaissements_prets WHERE pret_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pretId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Compte le nombre de décaissements pour une réunion
     */
    public int countByReunionId(long reunionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM decaissements_prets WHERE reunion_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reunionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Map un ResultSet vers un objet DecaissementPret
     */
    private DecaissementPret mapResultSetToDecaissement(ResultSet rs) throws SQLException {
        DecaissementPret decaissement = new DecaissementPret();

        decaissement.setId(rs.getLong("id"));
        decaissement.setNumeroDecaissement(rs.getString("numero_decaissement"));
        decaissement.setMontant(rs.getBigDecimal("montant"));
        decaissement.setDateDecaissement(rs.getTimestamp("date_decaissement").toLocalDateTime());
        decaissement.setModePaiement(rs.getString("mode_paiement"));
        decaissement.setObservations(rs.getString("observations"));
        decaissement.setPretId(rs.getLong("pret_id"));
        decaissement.setReunionId(rs.getLong("reunion_id"));

        long encaisseParId = rs.getLong("encaisse_par_id");
        if (!rs.wasNull()) {
            decaissement.setEncaisseParId(encaisseParId);
        }

        long approuveParId = rs.getLong("approuve_par_id");
        if (!rs.wasNull()) {
            decaissement.setApprouveParId(approuveParId);
        }

        return decaissement;
    }
}