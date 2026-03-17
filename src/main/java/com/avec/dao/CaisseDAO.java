package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Caisse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des caisses dans la base de données
 */
public class CaisseDAO {

    private final DBConnection dbConnection;

    public CaisseDAO() {
        this.dbConnection = (DBConnection) DBConnection.getConnection();
    }

    /**
     * Crée une caisse pour une AVEC
     */
    public Caisse creerCaissePourAvec(long avecId) throws SQLException {
        String sql = "INSERT INTO caisses (code_securite, avec_id) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String codeSecurite = genererCodeSecurite();
            stmt.setString(1, codeSecurite);
            stmt.setLong(2, avecId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création de la caisse a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Caisse caisse = new Caisse();
                    caisse.setId(generatedKeys.getLong(1));
                    caisse.setCodeSecurite(codeSecurite);
                    caisse.setAvecId(avecId);
                    return caisse;
                } else {
                    throw new SQLException("La création de la caisse a échoué, aucun ID obtenu");
                }
            }
        }
    }

    /**
     * Trouve une caisse par son ID
     */
    public Caisse findById(long id) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCaisse(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve une caisse par l'ID de l'AVEC
     */
    public Caisse findByAvecId(long avecId) throws SQLException {
        String sql = "SELECT * FROM caisses WHERE avec_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCaisse(rs);
                }
            }
        }
        return null;
    }

    /**
     * Met à jour le code de sécurité d'une caisse
     */
    public boolean updateCodeSecurite(long caisseId, String nouveauCode) throws SQLException {
        String sql = "UPDATE caisses SET code_securite = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nouveauCode);
            stmt.setLong(2, caisseId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une caisse
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM caisses WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupère toutes les caisses
     */
    public List<Caisse> findAll() throws SQLException {
        List<Caisse> caisses = new ArrayList<>();
        String sql = "SELECT * FROM caisses ORDER BY id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                caisses.add(mapResultSetToCaisse(rs));
            }
        }
        return caisses;
    }

    /**
     * Vérifie si une AVEC a une caisse
     */
    public boolean existePourAvec(long avecId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM caisses WHERE avec_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Génère un code de sécurité aléatoire
     */
    private String genererCodeSecurite() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    /**
     * Map un ResultSet vers un objet Caisse
     */
    private Caisse mapResultSetToCaisse(ResultSet rs) throws SQLException {
        Caisse caisse = new Caisse();
        caisse.setId(rs.getLong("id"));
        caisse.setCodeSecurite(rs.getString("code_securite"));
        caisse.setAvecId(rs.getLong("avec_id"));
        return caisse;
    }
}