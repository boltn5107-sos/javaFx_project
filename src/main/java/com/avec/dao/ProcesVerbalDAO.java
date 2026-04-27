package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Membre;
import com.avec.model.ProcesVerbal;
import com.avec.model.Reunion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcesVerbalDAO {

    private Connection connection;
    private MembreDAO membreDAO;
    private ReunionDAO reunionDAO;

    public ProcesVerbalDAO() {
        try {
            this.connection = DBConnection.getConnection();
            this.membreDAO = new MembreDAO();
            this.reunionDAO = new ReunionDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProcesVerbal save(ProcesVerbal pv) throws SQLException {
        if (pv.getId() == null) {
            return insert(pv);
        } else {
            return update(pv);
        }
    }

    private ProcesVerbal insert(ProcesVerbal pv) throws SQLException {
        String sql = "INSERT INTO proces_verbal (reunion_id, contenu, decisions, observations, cree_par_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pv.getReunionId());
            stmt.setString(2, pv.getContenu());
            stmt.setString(3, pv.getDecisions());
            stmt.setString(4, pv.getObservations());
            
            if (pv.getCreeParId() != null) {
                stmt.setLong(5, pv.getCreeParId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            
            stmt.setTimestamp(6, Timestamp.valueOf(pv.getDateCreation() != null ? pv.getDateCreation() : LocalDateTime.now()));
            stmt.setTimestamp(7, Timestamp.valueOf(pv.getDateModification() != null ? pv.getDateModification() : LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'enregistrement du PV a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pv.setId(generatedKeys.getLong(1));
                }
            }
        }
        return pv;
    }

    private ProcesVerbal update(ProcesVerbal pv) throws SQLException {
        String sql = "UPDATE proces_verbal SET contenu = ?, decisions = ?, observations = ?, updated_at = ? " +
                     "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pv.getContenu());
            stmt.setString(2, pv.getDecisions());
            stmt.setString(3, pv.getObservations());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(5, pv.getId());

            stmt.executeUpdate();
        }
        return pv;
    }

    public ProcesVerbal findById(Long id) throws SQLException {
        String sql = "SELECT * FROM proces_verbal WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPV(rs);
                }
            }
        }
        return null;
    }

    public List<ProcesVerbal> findByReunionId(Long reunionId) throws SQLException {
        List<ProcesVerbal> pvs = new ArrayList<>();
        String sql = "SELECT * FROM proces_verbal WHERE reunion_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reunionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pvs.add(mapResultSetToPV(rs));
                }
            }
        }
        return pvs;
    }

    public List<ProcesVerbal> findAll() throws SQLException {
        List<ProcesVerbal> pvs = new ArrayList<>();
        String sql = "SELECT * FROM proces_verbal ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pvs.add(mapResultSetToPV(rs));
            }
        }
        return pvs;
    }

    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM proces_verbal WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private ProcesVerbal mapResultSetToPV(ResultSet rs) throws SQLException {
        ProcesVerbal pv = new ProcesVerbal();
        
        pv.setId(rs.getLong("id"));
        pv.setReunionId(rs.getLong("reunion_id"));
        pv.setContenu(rs.getString("contenu"));
        pv.setDecisions(rs.getString("decisions"));
        pv.setObservations(rs.getString("observations"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            pv.setDateCreation(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            pv.setDateModification(updatedAt.toLocalDateTime());
        }
        
        long creeParId = rs.getLong("cree_par_id");
        if (!rs.wasNull()) {
            pv.setCreeParId(creeParId);
        }
        
        return pv;
    }
    
    public ProcesVerbal loadRelations(ProcesVerbal pv) throws SQLException {
        if (pv == null) return null;
        
        if (pv.getReunionId() != null) {
            Reunion reunion = reunionDAO.chercherId(pv.getReunionId());
            pv.setReunion(reunion);
        }
        
        if (pv.getCreeParId() != null) {
            Membre membre = membreDAO.findById(pv.getCreeParId());
            pv.setCreePar(membre);
        }
        
        return pv;
    }
}