package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import com.avec.model.Membre;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des membres dans la base de données
 */
public class MembreDAO {

    /**
     * Insère un nouveau membre
     */
    public Membre insert(Membre membre) throws SQLException {
        String sql = "INSERT INTO membres (nom, prenom, numero_carte, statut, date_adhesion, " +
                "profession, village, telephone, role_comite, role_cle, total_epargne, " +
                "total_pret_en_cours, nombre_parts, avec_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getNumeroCarte());
            stmt.setString(4, membre.getStatut().name());
            stmt.setDate(5, Date.valueOf(membre.getDateAdhesion()));
            stmt.setString(6, membre.getProfession());
            stmt.setString(7, membre.getVillage());
            stmt.setString(8, membre.getTelephone());
            stmt.setString(9, membre.getRoleComite().name());
            stmt.setString(10, membre.getRoleCle().name());
            stmt.setBigDecimal(11, membre.getTotalEpargne());
            stmt.setBigDecimal(12, membre.getTotalPretEnCours());
            stmt.setInt(13, membre.getNombreParts());
            stmt.setLong(14, membre.getAvecId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du membre a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    membre.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création du membre a échoué, aucun ID obtenu");
                }
            }
        }
        return membre;
    }

    /**
     * Met à jour un membre existant
     */
    public boolean update(Membre membre) throws SQLException {
        String sql = "UPDATE membres SET nom = ?, prenom = ?, numero_carte = ?, statut = ?, " +
                "date_adhesion = ?, profession = ?, village = ?, telephone = ?, " +
                "role_comite = ?, role_cle = ?, total_epargne = ?, total_pret_en_cours = ?, " +
                "nombre_parts = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getNumeroCarte());
            stmt.setString(4, membre.getStatut().name());
            stmt.setDate(5, Date.valueOf(membre.getDateAdhesion()));
            stmt.setString(6, membre.getProfession());
            stmt.setString(7, membre.getVillage());
            stmt.setString(8, membre.getTelephone());
            stmt.setString(9, membre.getRoleComite().name());
            stmt.setString(10, membre.getRoleCle().name());
            stmt.setBigDecimal(11, membre.getTotalEpargne());
            stmt.setBigDecimal(12, membre.getTotalPretEnCours());
            stmt.setInt(13, membre.getNombreParts());
            stmt.setLong(14, membre.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime un membre par son ID
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM membres WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve un membre par son ID
     */
    public Membre findById(long id) throws SQLException {
        String sql = "SELECT * FROM membres WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve tous les membres d'une AVEC
     */
    public List<Membre> findByAvecId(long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE avec_id = ? ORDER BY nom, prenom";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Trouve un membre par son numéro de carte
     */
    public Membre findByNumeroCarte(String numeroCarte) throws SQLException {
        String sql = "SELECT * FROM membres WHERE numero_carte = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroCarte);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve les membres par rôle au comité
     */
    public List<Membre> findByRoleComite(RoleComite role) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE role_comite = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Trouve les gardiens de clés d'une AVEC
     */
    public List<Membre> findGardiensCles(long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE avec_id = ? AND role_cle != 'AUCUN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Trouve le comité de gestion d'une AVEC
     */
    public List<Membre> findComiteGestion(long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE avec_id = ? AND role_comite != 'AUCUN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Compte le nombre de membres actifs dans une AVEC
     */
    public int countActifsByAvecId(long avecId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM membres WHERE avec_id = ? AND statut = 'ACTIF'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Met à jour le rôle au comité
     */
    public boolean updateRoleComite(long membreId, RoleComite role) throws SQLException {
        String sql = "UPDATE membres SET role_comite = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());
            stmt.setLong(2, membreId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour le rôle de gardien de clé
     */
    public boolean updateRoleCle(long membreId, RoleDetenteurCle role) throws SQLException {
        String sql = "UPDATE membres SET role_cle = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());
            stmt.setLong(2, membreId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Réinitialise tous les rôles du comité pour une AVEC
     */
    public boolean resetAllRolesComite(long avecId) throws SQLException {
        String sql = "UPDATE membres SET role_comite = 'AUCUN' WHERE avec_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Réinitialise tous les rôles de gardien de clé pour une AVEC
     */
    public boolean resetAllRolesCle(long avecId) throws SQLException {
        String sql = "UPDATE membres SET role_cle = 'AUCUN' WHERE avec_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour les totaux d'un membre
     */
    public boolean updateTotaux(long membreId, BigDecimal epargne, BigDecimal prets, int nombreParts) throws SQLException {
        String sql = "UPDATE membres SET total_epargne = ?, total_pret_en_cours = ?, nombre_parts = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, epargne);
            stmt.setBigDecimal(2, prets);
            stmt.setInt(3, nombreParts);
            stmt.setLong(4, membreId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Recherche des membres par nom
     */
    public List<Membre> searchByNom(long avecId, String recherche) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE avec_id = ? AND (nom LIKE ? OR prenom LIKE ? OR nom_complet LIKE ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + recherche + "%";
            stmt.setLong(1, avecId);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Map un ResultSet vers un objet Membre
     */
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();

        membre.setId(rs.getLong("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setNumeroCarte(rs.getString("numero_carte"));
        membre.setStatut(StatutMembre.valueOf(rs.getString("statut")));
        membre.setDateAdhesion(rs.getDate("date_adhesion").toLocalDate());
        membre.setProfession(rs.getString("profession"));
        membre.setVillage(rs.getString("village"));
        membre.setTelephone(rs.getString("telephone"));
        membre.setRoleComite(RoleComite.valueOf(rs.getString("role_comite")));
        membre.setRoleCle(RoleDetenteurCle.valueOf(rs.getString("role_cle")));
        membre.setTotalEpargne(rs.getBigDecimal("total_epargne"));
        membre.setTotalPretEnCours(rs.getBigDecimal("total_pret_en_cours"));
        membre.setNombreParts(rs.getInt("nombre_parts"));
        membre.setAvecId(rs.getLong("avec_id"));

        return membre;
    }
}