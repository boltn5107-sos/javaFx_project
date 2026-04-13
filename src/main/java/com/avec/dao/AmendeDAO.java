package com.avec.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.model.Amende;
import com.avec.model.Membre;
import com.avec.model.Reunion;
import com.avec.model.TypeInfraction;

public class AmendeDAO {

    private Connection connection;
    private MembreDAO membreDAO;
    private ReunionDAO reunionDAO;
    private TypeInfractionDAO typeInfractionDAO;

    public AmendeDAO() {
        try {
            this.connection = DBConnection.getConnection();
            this.membreDAO = new MembreDAO();
            this.reunionDAO = new ReunionDAO();
            this.typeInfractionDAO = new TypeInfractionDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enregistre une nouvelle amende
     */
    public Amende insert(Amende amende) throws SQLException {
        String sql = "INSERT INTO amende (membre_id, reunion_id, type_infraction_id, montant, observations) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, amende.getMembreId());
            stmt.setLong(2, amende.getReunionId());
            stmt.setLong(3, amende.getTypeInfractionId());
            stmt.setBigDecimal(4, amende.getMontant());
            stmt.setString(5, amende.getObservations());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création de l'amende a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    amende.setId(generatedKeys.getLong(1));
                }
            }
        }
        return amende;
    }

    /**
     * Marque une amende comme payée
     */
    public boolean marquerPayee(Long amendeId) throws SQLException {
        String sql = "UPDATE amende SET est_paye = TRUE, date_paiement = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, amendeId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupère toutes les amendes d'un membre
     */
    public List<Amende> findByMembreId(Long membreId) throws SQLException {
        List<Amende> amendes = new ArrayList<>();
        String sql = "SELECT * FROM amende WHERE membre_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    amendes.add(mapResultSetToAmende(rs));
                }
            }
        }
        return amendes;
    }

    /**
     * Récupère toutes les amendes d'une réunion
     */
    public List<Amende> findByReunionId(Long reunionId) throws SQLException {
        List<Amende> amendes = new ArrayList<>();
        String sql = "SELECT * FROM amende WHERE reunion_id = ? ORDER BY montant DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reunionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    amendes.add(mapResultSetToAmende(rs));
                }
            }
        }
        return amendes;
    }

    /**
     * Récupère toutes les amendes non payées
     */
    public List<Amende> findNonPayees() throws SQLException {
        List<Amende> amendes = new ArrayList<>();
        String sql = "SELECT * FROM amende WHERE est_paye = FALSE ORDER BY created_at ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                amendes.add(mapResultSetToAmende(rs));
            }
        }
        return amendes;
    }

    /**
     * Calcule le total des amendes non payées pour un membre
     */
    public BigDecimal getTotalNonPayeParMembre(Long membreId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant), 0) FROM amende WHERE membre_id = ? AND est_paye = FALSE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, membreId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Supprime une amende
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM amende WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Map ResultSet vers Amende
     */
    private Amende mapResultSetToAmende(ResultSet rs) throws SQLException {
        Amende amende = new Amende();
        
        amende.setId(rs.getLong("id"));
        amende.setMembreId(rs.getLong("membre_id"));
        amende.setReunionId(rs.getLong("reunion_id"));
        amende.setTypeInfractionId(rs.getLong("type_infraction_id"));
        amende.setMontant(rs.getBigDecimal("montant"));
        amende.setEstPaye(rs.getBoolean("est_paye"));
        amende.setObservations(rs.getString("observations"));
        
        Timestamp datePaiement = rs.getTimestamp("date_paiement");
        if (datePaiement != null) {
            amende.setDatePaiement(datePaiement.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            amende.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        // Charger les objets associés
        Membre membre = membreDAO.findById(amende.getMembreId());
        amende.setMembre(membre);
        
        Reunion reunion = reunionDAO.chercherId(amende.getReunionId());
        amende.setReunion(reunion);
        
        TypeInfraction typeInfraction = typeInfractionDAO.findById(amende.getTypeInfractionId());
        amende.setTypeInfraction(typeInfraction);
        
        return amende;
    }
}