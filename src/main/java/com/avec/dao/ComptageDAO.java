package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Avec;
import com.avec.model.Comptage;
import com.avec.model.Membre;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComptageDAO {

    private Connection connection;
    private MembreDAO membreDAO;
    private AvecDAO avecDAO;

    public ComptageDAO() {
        try {
            this.connection = DBConnection.getConnection();
            this.membreDAO = new MembreDAO();
            this.avecDAO = new AvecDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Comptage save(Comptage comptage) throws SQLException {
        if (comptage.getId() == null) {
            return insert(comptage);
        } else {
            return update(comptage);
        }
    }

    private Comptage insert(Comptage comptage) throws SQLException {
        String sql = "INSERT INTO comptage (avec_id, date_comptage, fond_credit, amendes, total, compteur_id, observations, est_confirme, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, comptage.getAvecId());
            stmt.setDate(2, Date.valueOf(comptage.getDateComptage()));
            stmt.setBigDecimal(3, comptage.getFondCredit());
            stmt.setBigDecimal(4, comptage.getAmendes());
            stmt.setBigDecimal(5, comptage.getTotal());
            
            if (comptage.getCompteurId() != null) {
                stmt.setLong(6, comptage.getCompteurId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setString(7, comptage.getObservations());
            stmt.setBoolean(8, comptage.isEstConfirme());
            stmt.setTimestamp(9, Timestamp.valueOf(comptage.getCreatedAt() != null ? comptage.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'enregistrement du comptage a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comptage.setId(generatedKeys.getLong(1));
                }
            }
        }
        return comptage;
    }

    private Comptage update(Comptage comptage) throws SQLException {
        String sql = "UPDATE comptage SET fond_credit = ?, amendes = ?, total = ?, observations = ?, est_confirme = ?, updated_at = ? " +
                     "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, comptage.getFondCredit());
            stmt.setBigDecimal(2, comptage.getAmendes());
            stmt.setBigDecimal(3, comptage.getTotal());
            stmt.setString(4, comptage.getObservations());
            stmt.setBoolean(5, comptage.isEstConfirme());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(7, comptage.getId());

            stmt.executeUpdate();
        }
        return comptage;
    }

    public Comptage findById(Long id) throws SQLException {
        String sql = "SELECT * FROM comptage WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComptage(rs);
                }
            }
        }
        return null;
    }

    public List<Comptage> findByAvecId(Long avecId) throws SQLException {
        List<Comptage> comptages = new ArrayList<>();
        String sql = "SELECT * FROM comptage WHERE avec_id = ? ORDER BY date_comptage DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avecId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comptages.add(mapResultSetToComptage(rs));
                }
            }
        }
        return comptages;
    }

    public List<Comptage> findAll() throws SQLException {
        List<Comptage> comptages = new ArrayList<>();
        String sql = "SELECT * FROM comptage ORDER BY date_comptage DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                comptages.add(mapResultSetToComptage(rs));
            }
        }
        return comptages;
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM comptage WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Comptage findLastByAvecId(Long avecId) throws SQLException {
        String sql = "SELECT * FROM comptage WHERE avec_id = ? ORDER BY date_comptage DESC LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avecId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComptage(rs);
                }
            }
        }
        return null;
    }

    private Comptage mapResultSetToComptage(ResultSet rs) throws SQLException {
        Comptage comptage = new Comptage();
        
        comptage.setId(rs.getLong("id"));
        comptage.setAvecId(rs.getLong("avec_id"));
        
        Date dateComptage = rs.getDate("date_comptage");
        if (dateComptage != null) {
            comptage.setDateComptage(dateComptage.toLocalDate());
        }
        
        comptage.setFondCredit(rs.getBigDecimal("fond_credit"));
        comptage.setAmendes(rs.getBigDecimal("amendes"));
        comptage.setTotal(rs.getBigDecimal("total"));
        
        long compteurId = rs.getLong("compteur_id");
        if (!rs.wasNull()) {
            comptage.setCompteurId(compteurId);
        }
        
        comptage.setObservations(rs.getString("observations"));
        comptage.setEstConfirme(rs.getBoolean("est_confirme"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            comptage.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            comptage.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        if (comptage.getAvecId() != null) {
            Avec avec = avecDAO.findById(comptage.getAvecId());
            comptage.setAvec(avec);
        }
        
        if (comptage.getCompteurId() != null) {
            Membre membre = membreDAO.findById(comptage.getCompteurId());
            comptage.setCompteur(membre);
        }
        
        return comptage;
    }
}