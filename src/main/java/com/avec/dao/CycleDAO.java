package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.StatutCycle;
import com.avec.model.Cycle;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CycleDAO {

    /**
     * Insère un nouveau cycle
     */
    public Cycle insert(Cycle cycle) throws SQLException {
        String sql = "INSERT INTO cycles (date_debut, date_fin_prevue, date_fin_reelle, statut, " +
                "fonds_credit_final, total_parts_achetees, valeur_part, numero_cycle, avec_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(cycle.getDateDebut()));
            stmt.setDate(2, cycle.getDateFinPrevue() != null ? Date.valueOf(cycle.getDateFinPrevue()) : null);
            stmt.setDate(3, cycle.getDateFinReelle() != null ? Date.valueOf(cycle.getDateFinReelle()) : null);
            stmt.setString(4, cycle.getStatut().name());
            stmt.setBigDecimal(5, cycle.getFondsDeCreditFinal());
            stmt.setInt(6, cycle.getTotalPartsAchetees());
            stmt.setBigDecimal(7, cycle.getValeurPart());
            stmt.setInt(8, cycle.getNumeroCycle());
            stmt.setLong(9, cycle.getAvecId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du cycle a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cycle.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création du cycle a échoué, aucun ID obtenu");
                }
            }
        }
        return cycle;
    }

    /**
     * Met à jour un cycle existant
     */
    public boolean update(Cycle cycle) throws SQLException {
        String sql = "UPDATE cycles SET date_debut = ?, date_fin_prevue = ?, date_fin_reelle = ?, " +
                "statut = ?, fonds_credit_final = ?, total_parts_achetees = ?, valeur_part = ?, " +
                "numero_cycle = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(cycle.getDateDebut()));
            stmt.setDate(2, cycle.getDateFinPrevue() != null ? Date.valueOf(cycle.getDateFinPrevue()) : null);
            stmt.setDate(3, cycle.getDateFinReelle() != null ? Date.valueOf(cycle.getDateFinReelle()) : null);
            stmt.setString(4, cycle.getStatut().name());
            stmt.setBigDecimal(5, cycle.getFondsDeCreditFinal());
            stmt.setInt(6, cycle.getTotalPartsAchetees());
            stmt.setBigDecimal(7, cycle.getValeurPart());
            stmt.setInt(8, cycle.getNumeroCycle());
            stmt.setLong(9, cycle.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve un cycle par son ID
     */
    public Cycle findById(long id) throws SQLException {
        String sql = "SELECT * FROM cycles WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCycle(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve les cycles d'une AVEC
     */
    public List<Cycle> findByAvecId(long avecId) throws SQLException {
        List<Cycle> cycles = new ArrayList<>();
        String sql = "SELECT * FROM cycles WHERE avec_id = ? ORDER BY numero_cycle DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cycles.add(mapResultSetToCycle(rs));
                }
            }
        }
        return cycles;
    }

    /**
     * Trouve le cycle en cours d'une AVEC
     */
    public Cycle findEnCoursByAvecId(long avecId) throws SQLException {
        String sql = "SELECT * FROM cycles WHERE avec_id = ? AND statut = 'EN_COURS'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCycle(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve tous les cycles
     */
    public List<Cycle> findAll() throws SQLException {
        List<Cycle> cycles = new ArrayList<>();
        String sql = "SELECT * FROM cycles ORDER BY date_debut DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cycles.add(mapResultSetToCycle(rs));
            }
        }
        return cycles;
    }

    /**
     * Supprime un cycle
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM cycles WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Map un ResultSet vers un objet Cycle
     */
    private Cycle mapResultSetToCycle(ResultSet rs) throws SQLException {
        Cycle cycle = new Cycle();

        cycle.setId(rs.getLong("id"));
        cycle.setDateDebut(rs.getDate("date_debut").toLocalDate());

        Date dateFinPrevue = rs.getDate("date_fin_prevue");
        if (dateFinPrevue != null) {
            cycle.setDateFinPrevue(dateFinPrevue.toLocalDate());
        }

        Date dateFinReelle = rs.getDate("date_fin_reelle");
        if (dateFinReelle != null) {
            cycle.setDateFinReelle(dateFinReelle.toLocalDate());
        }

        cycle.setStatut(StatutCycle.valueOf(rs.getString("statut")));
        cycle.setFondsDeCreditFinal(rs.getBigDecimal("fonds_credit_final"));
        cycle.setTotalPartsAchetees(rs.getInt("total_parts_achetees"));
        cycle.setValeurPart(rs.getBigDecimal("valeur_part"));
        cycle.setNumeroCycle(rs.getInt("numero_cycle"));
        cycle.setAvecId(rs.getLong("avec_id"));

        return cycle;
    }
}