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
        String sql = "INSERT INTO cycle (dateDebut, dateFinPrevue, dateFinReelle, statut, " +
                "fondsCreditFinal, totalpartsAchetes, avec_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(cycle.getDateDebut()));
            stmt.setDate(2, cycle.getDateFinPrevue() != null ? Date.valueOf(cycle.getDateFinPrevue()) : null);
            stmt.setDate(3, cycle.getDateFinReelle() != null ? Date.valueOf(cycle.getDateFinReelle()) : null);
            stmt.setString(4, cycle.getStatut().name());
            stmt.setBigDecimal(5, cycle.getFondsDeCreditFinal());
            stmt.setInt(6, cycle.getTotalPartsAchetees() != null ? cycle.getTotalPartsAchetees() : 0);
            stmt.setLong(7, cycle.getAvecId());

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
        String sql = "UPDATE cycle SET dateDebut = ?, dateFinPrevue = ?, dateFinReelle = ?, " +
                "statut = ?, fondsCreditFinal = ?, totalpartsAchetes = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(cycle.getDateDebut()));
            stmt.setDate(2, cycle.getDateFinPrevue() != null ? Date.valueOf(cycle.getDateFinPrevue()) : null);
            stmt.setDate(3, cycle.getDateFinReelle() != null ? Date.valueOf(cycle.getDateFinReelle()) : null);
            stmt.setString(4, cycle.getStatut().name());
            stmt.setBigDecimal(5, cycle.getFondsDeCreditFinal());
            stmt.setInt(6, cycle.getTotalPartsAchetees() != null ? cycle.getTotalPartsAchetees() : 0);
            stmt.setLong(7, cycle.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve un cycle par son ID
     */
    public Cycle findById(long id) throws SQLException {
        String sql = "SELECT * FROM cycle WHERE id = ?";

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
        String sql = "SELECT * FROM cycle WHERE avec_id = ? ORDER BY id DESC";

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
        String sql = "SELECT * FROM cycle WHERE avec_id = ? AND statut = 'EN_COURS'";

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
        String sql = "SELECT * FROM cycle ORDER BY id DESC";

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
        String sql = "DELETE FROM cycle WHERE id = ?";

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
        cycle.setDateDebut(rs.getDate("dateDebut").toLocalDate());

        Date dateFinPrevue = rs.getDate("dateFinPrevue");
        if (dateFinPrevue != null) {
            cycle.setDateFinPrevue(dateFinPrevue.toLocalDate());
        }

        Date dateFinReelle = rs.getDate("dateFinReelle");
        if (dateFinReelle != null) {
            cycle.setDateFinReelle(dateFinReelle.toLocalDate());
        }

        cycle.setStatut(StatutCycle.valueOf(rs.getString("statut")));
        cycle.setFondsDeCreditFinal(rs.getBigDecimal("fondsCreditFinal"));
        cycle.setTotalPartsAchetees(rs.getInt("totalpartsAchetes"));
        cycle.setAvecId(rs.getLong("avec_id"));

        return cycle;
    }
}