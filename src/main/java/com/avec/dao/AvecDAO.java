package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.PhaseCycle;
import com.avec.enums.StatutAvec;
import com.avec.model.Avec;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des AVEC
 * Table: avec
 */
public class AvecDAO {

    public AvecDAO() {
       
    }


    /**
     * Insère une nouvelle AVEC
     */
    public Avec insert(Avec avec) throws SQLException {

        String sql = "INSERT INTO avec (nom, codeUnique, statut, dateCreation, " +
                "nombreMembreMax, prixPart, tauxFraisServiceMensuel, " +
                "phaseCourante,  " +
                "agentVillageois_id, agentTerrain_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println(">>> DAO: Debut insert()");
        System.out.println(">>> DAO: avec.nom = " + avec.getNom());
        System.out.println(">>> DAO: avec.agentVillageoisId = " + avec.getAgentVillageoisId());
        
              

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, avec.getNom());
            stmt.setString(2, avec.getCodeUnique());
            stmt.setString(3, avec.getStatut().name());
            stmt.setDate(4, Date.valueOf(avec.getDateCreation()));
            stmt.setInt(5, avec.getNombreMembresMax());
            stmt.setBigDecimal(6, avec.getPrixPart());
            stmt.setBigDecimal(7, avec.getTauxFraisServiceMensuel());
            stmt.setString(8, avec.getPhaseCourante().name());
            stmt.setLong(9, avec.getAgentVillageoisId());

            if (avec.getAgentTerrainId() != null) {
                stmt.setLong(10, avec.getAgentTerrainId());
            } else {
                stmt.setNull(10, Types.BIGINT);
            }

            System.out.println(">>> DAO: Execution INSERT...");
            int affectedRows = stmt.executeUpdate();
            System.out.println(">>> DAO: affectedRows = " + affectedRows);
            
            if (affectedRows == 0) {
                throw new SQLException("La création de l'AVEC a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    avec.setId(generatedKeys.getLong(1));
                    System.out.println(">>> DAO: ID genere = " + avec.getId());
                }
            }
        }
        System.out.println(">>> DAO: Fin insert() - SUCCESS");
        return avec;
    }

    /**
     * Met à jour une AVEC existante
     */
    public boolean update(Avec avec) throws SQLException {

        String sql = "UPDATE avec SET nom = ?, codeUnique = ?, statut = ?, " +
                "nombreMembreMax = ?, prixPart = ?, tauxFraisServiceMensuel = ?, " +
                "phaseCourante = ?, " +
                "agentVillageois_id = ?, agentTerrain_id = ? WHERE id = ?";

        System.out.println(">>> DAO UPDATE: Debut");
        System.out.println(">>> DAO UPDATE: id=" + avec.getId() + ", nom=" + avec.getNom());
        

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, avec.getNom());
            stmt.setString(2, avec.getCodeUnique());
            stmt.setString(3, avec.getStatut().name());
            stmt.setInt(4, avec.getNombreMembresMax());
            stmt.setBigDecimal(5, avec.getPrixPart());
            stmt.setBigDecimal(6, avec.getTauxFraisServiceMensuel());
            stmt.setString(7, avec.getPhaseCourante().name());
            stmt.setLong(8, avec.getAgentVillageoisId());

            if (avec.getAgentTerrainId() != null) {
                stmt.setLong(9, avec.getAgentTerrainId());
            } else {
                stmt.setNull(9, Types.BIGINT);
            }

            stmt.setLong(10, avec.getId());

            System.out.println(">>> DAO UPDATE: Execution UPDATE...");
            int rows = stmt.executeUpdate();
            System.out.println(">>> DAO UPDATE: rows affected = " + rows);
            return rows > 0;
        }
    }

    /**
     * Supprime une AVEC par son ID
     */
    public boolean delete(long id) throws SQLException {

        // Supprimer d'abord les dépendances
        String deleteCaisse = "DELETE FROM caisse WHERE avec_id = ?";
        String deleteMembres = "DELETE FROM membre WHERE avec_id = ?";
        String deleteCycles = "DELETE FROM cycle WHERE avec_id = ?";
        String deleteVisites = "DELETE FROM visite WHERE avec_id = ?";
        String deleteRegles = "DELETE FROM regle WHERE avec_id = ?";
        String deleteAvec = "DELETE FROM avec WHERE id = ?";


        String sql = "DELETE FROM avec WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve une AVEC par son ID
     */
    public Avec findById(long id) throws SQLException {
        String sql = "SELECT * FROM avec WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAvec(rs);
                }
            }
        }
        return null;
    }

    /**
     * Vérifie si une AVEC existe par son ID
     */
    public boolean existsById(long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM avec WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Trouve une AVEC par son code unique
     */
    public Avec findByCodeUnique(String codeUnique) throws SQLException {

        String sql = "SELECT * FROM avec WHERE codeUnique = ?";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codeUnique);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAvec(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve toutes les AVEC
     */
    public List<Avec> findAll() throws SQLException {
        List<Avec> avecs = new ArrayList<>();

        String sql = "SELECT * FROM avec ORDER BY dateCreation DESC";


        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                avecs.add(mapResultSetToAvec(rs));
            }
        }
        return avecs;
    }

    /**
     * Trouve les AVEC par statut
     */
    public List<Avec> findByStatut(StatutAvec statut) throws SQLException {
        List<Avec> avecs = new ArrayList<>();

        String sql = "SELECT * FROM avec WHERE statut = ? ORDER BY dateCreation DESC";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avecs.add(mapResultSetToAvec(rs));
                }
            }
        }
        return avecs;
    }

    /**
     * Trouve les AVEC par phase
     */
    public List<Avec> findByPhase(PhaseCycle phase) throws SQLException {
        List<Avec> avecs = new ArrayList<>();

        String sql = "SELECT * FROM avec WHERE phaseCourante = ? ORDER BY dateCreation DESC";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phase.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avecs.add(mapResultSetToAvec(rs));
                }
            }
        }
        return avecs;
    }

    /**
     * Trouve les AVEC par agent villageois
     */
    public List<Avec> findByAgentVillageoisId(long agentId) throws SQLException {
        List<Avec> avecs = new ArrayList<>();

        String sql = "SELECT * FROM avec WHERE agentVillageois_id = ? ORDER BY dateCreation DESC";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, agentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avecs.add(mapResultSetToAvec(rs));
                }
            }
        }
        return avecs;
    }

    /**
     * Trouve les AVEC par agent terrain
     */
    public List<Avec> findByAgentTerrainId(long agentId) throws SQLException {
        List<Avec> avecs = new ArrayList<>();


        String sql = "SELECT * FROM avec WHERE agentTerrain_id = ? ORDER BY dateCreation DESC";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, agentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avecs.add(mapResultSetToAvec(rs));
                }
            }
        }
        return avecs;
    }

    /**
     * Recherche des AVEC par nom
     */
    public List<Avec> searchByNom(String recherche) throws SQLException {
        List<Avec> avecs = new ArrayList<>();
        String sql = "SELECT * FROM avec WHERE nom LIKE ? ORDER BY nom";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + recherche + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avecs.add(mapResultSetToAvec(rs));
                }
            }
        }
        return avecs;
    }

    /**
     * Met à jour la phase d'une AVEC
     */
    public boolean updatePhase(long avecId, PhaseCycle nouvellePhase) throws SQLException {
        String sql = "UPDATE avec SET phaseCourante = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nouvellePhase.name());
            stmt.setLong(2, avecId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour le statut d'une AVEC
     */
    public boolean updateStatut(long avecId, StatutAvec statut) throws SQLException {
        String sql = "UPDATE avec SET statut = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            stmt.setLong(2, avecId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour la prochaine réunion
     */
    public boolean updateProchaineReunion(long avecId, LocalDate date) throws SQLException {
        String sql = "UPDATE avec SET prochaine_reunion = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date != null ? Date.valueOf(date) : null);
            stmt.setLong(2, avecId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**

     * Compte le nombre total d'AVEC
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM avec";

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
     * Compte le nombre d'AVEC par statut
     */
    public int countByStatut(StatutAvec statut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM avec WHERE statut = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Map un ResultSet vers un objet Avec
     */
    private Avec mapResultSetToAvec(ResultSet rs) throws SQLException {
        Avec avec = new Avec();

        avec.setId(rs.getLong("id"));
        avec.setNom(rs.getString("nom"));
        avec.setCodeUnique(rs.getString("codeUnique"));

        // Gestion sécurisée du statut
        String statutStr = rs.getString("statut");
        try {
            avec.setStatut(StatutAvec.valueOf(statutStr));
        } catch (IllegalArgumentException e) {
            System.err.println("Statut invalide: " + statutStr + " - utilisation de EN_FORMATION par défaut");
            avec.setStatut(StatutAvec.EN_FORMATION);
        }

        avec.setDateCreation(rs.getDate("dateCreation").toLocalDate());
        avec.setNombreMembresMax(rs.getInt("nombreMembreMax"));
        avec.setPrixPart(rs.getBigDecimal("prixPart"));
        avec.setTauxFraisServiceMensuel(rs.getBigDecimal("tauxFraisServiceMensuel"));

        // Gestion sécurisée de la phase
        String phaseStr = rs.getString("phaseCourante");
        try {
            avec.setPhaseCourante(PhaseCycle.valueOf(phaseStr));
        } catch (IllegalArgumentException e) {
            System.err.println("Phase invalide: " + phaseStr + " - utilisation de PREPARATOIRE par défaut");
            avec.setPhaseCourante(PhaseCycle.PREPARATOIRE);
        }

        avec.setAgentVillageoisId(rs.getLong("agentVillageois_id"));

        long agentTerrainId = rs.getLong("agentTerrain_id");
        if (!rs.wasNull()) {
            avec.setAgentTerrainId(agentTerrainId);
        }

        return avec;
    }


    /**
     * Trouve une AVEC par son ID
     * @param id L'identifiant de l'AVEC à rechercher
     * @return L'AVEC trouvée ou null si non trouvée
     * @throws SQLException En cas d'erreur de base de données
     */
    public Avec findAvecById(Long id) throws SQLException {
        String sql = "SELECT * FROM avec WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAvec(rs);
                }
            }
        }
        return null;
    }

}