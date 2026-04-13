package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.TypeReunion;
import com.avec.model.AchatPart;
import com.avec.model.Reunion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AchatPartDAO {

    public AchatPart insert(AchatPart achat) throws SQLException {
        String sql = "INSERT INTO achatsparts(nombreParts, montantTotal, membre_id, reunion_id) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, achat.getNombreParts());
            stmt.setBigDecimal(2, achat.getMontantTotal());
            stmt.setObject(3, achat.getMembreId());
            
            Long reunionId = achat.getReunionId();
            if (reunionId != null && reunionId > 0) {
                stmt.setLong(4, reunionId);
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("L'achat de parts a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    achat.setId(generatedKeys.getLong(1));
                }
            }
        }
        return achat;
    }

    public boolean update(AchatPart achat) throws SQLException {
        String sql = "UPDATE achatsparts SET nombreParts = ?, montantTotal = ?, " +
                "membre_id = ?, reunion_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, achat.getNombreParts());
            stmt.setBigDecimal(2, achat.getMontantTotal());
            stmt.setObject(3, achat.getMembreId());
            
            Long reunionId = achat.getReunionId();
            if (reunionId != null && reunionId > 0) {
                stmt.setLong(4, reunionId);
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }
            stmt.setLong(5, achat.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM achatsparts WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public AchatPart findById(long id) throws SQLException {
        String sql = "SELECT * FROM achatsparts WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAchatPart(rs);
                }
            }
        }
        return null;
    }

    public List<AchatPart> findByMembreId(long membreId) throws SQLException {
        List<AchatPart> achats = new ArrayList<>();
        String sql = "SELECT a.*, r.date as reunion_date, r.type as reunion_type " +
                   "FROM achatsparts a " +
                   "LEFT JOIN reunion r ON a.reunion_id = r.id " +
                   "WHERE a.membre_id = ? ORDER BY a.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, membreId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AchatPart achat = mapResultSetToAchatPart(rs);
                    try {
                        java.sql.Date dateReunion = rs.getDate("reunion_date");
                        if (dateReunion != null) {
                            Reunion reunion = new Reunion();
                            reunion.setDate(dateReunion.toLocalDate());
                            reunion.setType(TypeReunion.valueOf(rs.getString("reunion_type")));
                            achat.setReunion(reunion);
                        }
                    } catch (Exception e) {}
                    achats.add(achat);
                }
            }
        }
        return achats;
    }

    public List<AchatPart> findByReunionId(long reunionId) throws SQLException {
        List<AchatPart> achats = new ArrayList<>();
        String sql = "SELECT * FROM achatsparts WHERE reunion_id = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reunionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achats.add(mapResultSetToAchatPart(rs));
                }
            }
        }
        return achats;
    }

    public List<AchatPart> findByAvecId(long avecId) throws SQLException {
        List<AchatPart> achats = new ArrayList<>();
        String sql = "SELECT ap.* FROM achatsparts ap " +
                "INNER JOIN membre m ON ap.membre_id = m.id " +
                "WHERE m.avec_id = ? ORDER BY ap.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achats.add(mapResultSetToAchatPart(rs));
                }
            }
        }
        return achats;
    }

    public int countByMembreId(long membreId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(nombreParts), 0) FROM achatsparts WHERE membre_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, membreId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public BigDecimal sumMontantByMembreId(long membreId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(montantTotal), 0) FROM achatsparts WHERE membre_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, membreId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal sumMontantByAvecId(long avecId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(ap.montantTotal), 0) FROM achatsparts ap " +
                "INNER JOIN membre m ON ap.membre_id = m.id " +
                "WHERE m.avec_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public int getPrixPart(Long avecId) throws SQLException {
        return 0;
    }

    private AchatPart mapResultSetToAchatPart(ResultSet rs) throws SQLException {
        AchatPart achat = new AchatPart();
        achat.setId(rs.getLong("id"));
        achat.setNombreParts(rs.getInt("nombreParts"));
        achat.setMontantTotal(rs.getBigDecimal("montantTotal"));

        long membreId = rs.getLong("membre_id");
        if (!rs.wasNull()) {
            achat.setMembreId(membreId);
        }

        long reunionId = rs.getLong("reunion_id");
        if (!rs.wasNull()) {
            achat.setReunionId(reunionId);
        }

        return achat;
    }
}