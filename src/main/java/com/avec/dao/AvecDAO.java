package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.PhaseCycle;
import com.avec.enums.StatutAvec;
import com.avec.enums.JourReunion;
import com.avec.model.Avec;
import com.avec.model.AgentVillageois;
import com.avec.model.AgentTerrain;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des AVEC dans la base de données
 */
public class AvecDAO {

    private final DBConnection dbConnection;

    public AvecDAO() {
        this.dbConnection = (DBConnection) DBConnection.getConnection();
    }

    /**
     * Insère une nouvelle AVEC
     */
    public Avec insert(Avec avec) throws SQLException {
        String sql = "INSERT INTO avecs (nom, code_unique, statut, date_creation, " +
                "nombre_membres_max, prix_part, taux_frais_service_mensuel, " +
                "phase_courante, date_debut_cycle, date_fin_cycle_prevue, " +
                "lieu_reunion, jour_reunion, heure_reunion, prochaine_reunion, " +
                "cotisation_caisse_solidarite, caisse_solidarite_active, " +
                "agent_villageois_id, agent_terrain_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, avec.getNom());
            stmt.setString(2, avec.getCodeUnique());
            stmt.setString(3, avec.getStatut().name());
            stmt.setDate(4, Date.valueOf(avec.getDateCreation()));
            stmt.setInt(5, avec.getNombreMembresMax());
            stmt.setBigDecimal(6, avec.getPrixPart());
            stmt.setBigDecimal(7, avec.getTauxFraisServiceMensuel());
            stmt.setString(8, avec.getPhaseCourante().name());
            stmt.setDate(9, avec.getDateDebutCycle() != null ? Date.valueOf(avec.getDateDebutCycle()) : null);
            stmt.setDate(10, avec.getDateFinCyclePrevue() != null ? Date.valueOf(avec.getDateFinCyclePrevue()) : null);
            stmt.setString(11, avec.getLieuReunion());
            stmt.setString(12, avec.getJourReunion() != null ? avec.getJourReunion().name() : null);
            stmt.setTime(13, avec.getHeureReunion() != null ? Time.valueOf(avec.getHeureReunion()) : null);
            stmt.setDate(14, avec.getProchaineReunion() != null ? Date.valueOf(avec.getProchaineReunion()) : null);
            stmt.setBigDecimal(15, avec.getCotisationCaisseSolidarite());
            stmt.setBoolean(16, avec.isCaisseSolidariteActive());
            stmt.setLong(17, avec.getAgentVillageoisId());
            stmt.setLong(18, avec.getAgentTerrainId() != null ? avec.getAgentTerrainId() : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création de l'AVEC a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    avec.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création de l'AVEC a échoué, aucun ID obtenu");
                }
            }
        }

        // Créer la caisse associée
        CaisseDAO caisseDAO = new CaisseDAO();
        caisseDAO.creerCaissePourAvec(avec.getId());

        return avec;
    }

    /**
     * Met à jour une AVEC existante
     */
    public boolean update(Avec avec) throws SQLException {
        String sql = "UPDATE avecs SET nom = ?, code_unique = ?, statut = ?, " +
                "nombre_membres_max = ?, prix_part = ?, taux_frais_service_mensuel = ?, " +
                "phase_courante = ?, date_debut_cycle = ?, date_fin_cycle_prevue = ?, " +
                "lieu_reunion = ?, jour_reunion = ?, heure_reunion = ?, prochaine_reunion = ?, " +
                "cotisation_caisse_solidarite = ?, caisse_solidarite_active = ?, " +
                "agent_villageois_id = ?, agent_terrain_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, avec.getNom());
            stmt.setString(2, avec.getCodeUnique());
            stmt.setString(3, avec.getStatut().name());
            stmt.setInt(4, avec.getNombreMembresMax());
            stmt.setBigDecimal(5, avec.getPrixPart());
            stmt.setBigDecimal(6, avec.getTauxFraisServiceMensuel());
            stmt.setString(7, avec.getPhaseCourante().name());
            stmt.setDate(8, avec.getDateDebutCycle() != null ? Date.valueOf(avec.getDateDebutCycle()) : null);
            stmt.setDate(9, avec.getDateFinCyclePrevue() != null ? Date.valueOf(avec.getDateFinCyclePrevue()) : null);
            stmt.setString(10, avec.getLieuReunion());
            stmt.setString(11, avec.getJourReunion() != null ? avec.getJourReunion().name() : null);
            stmt.setTime(12, avec.getHeureReunion() != null ? Time.valueOf(avec.getHeureReunion()) : null);
            stmt.setDate(13, avec.getProchaineReunion() != null ? Date.valueOf(avec.getProchaineReunion()) : null);
            stmt.setBigDecimal(14, avec.getCotisationCaisseSolidarite());
            stmt.setBoolean(15, avec.isCaisseSolidariteActive());
            stmt.setLong(16, avec.getAgentVillageoisId());
            stmt.setLong(17, avec.getAgentTerrainId() != null ? avec.getAgentTerrainId() : null);
            stmt.setLong(18, avec.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une AVEC par son ID
     */
    public boolean delete(long id) throws SQLException {
        // Supprimer d'abord les dépendances
        String deleteCaisse = "DELETE FROM caisses WHERE avec_id = ?";
        String deleteMembres = "DELETE FROM membres WHERE avec_id = ?";
        String deleteCycles = "DELETE FROM cycles WHERE avec_id = ?";
        String deleteVisites = "DELETE FROM visites WHERE avec_id = ?";
        String deleteRegles = "DELETE FROM regles WHERE avec_id = ?";
        String deleteAvec = "DELETE FROM avecs WHERE id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCaisse = conn.prepareStatement(deleteCaisse);
                 PreparedStatement stmtMembres = conn.prepareStatement(deleteMembres);
                 PreparedStatement stmtCycles = conn.prepareStatement(deleteCycles);
                 PreparedStatement stmtVisites = conn.prepareStatement(deleteVisites);
                 PreparedStatement stmtRegles = conn.prepareStatement(deleteRegles);
                 PreparedStatement stmtAvec = conn.prepareStatement(deleteAvec)) {

                stmtCaisse.setLong(1, id);
                stmtCaisse.executeUpdate();

                stmtMembres.setLong(1, id);
                stmtMembres.executeUpdate();

                stmtCycles.setLong(1, id);
                stmtCycles.executeUpdate();

                stmtVisites.setLong(1, id);
                stmtVisites.executeUpdate();

                stmtRegles.setLong(1, id);
                stmtRegles.executeUpdate();

                stmtAvec.setLong(1, id);
                int result = stmtAvec.executeUpdate();

                conn.commit();
                return result > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Trouve une AVEC par son ID
     */
    public Avec findById(long id) throws SQLException {
        String sql = "SELECT * FROM avecs WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
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
     * Trouve une AVEC par son code unique
     */
    public Avec findByCodeUnique(String codeUnique) throws SQLException {
        String sql = "SELECT * FROM avecs WHERE code_unique = ?";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT * FROM avecs ORDER BY date_creation DESC";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT * FROM avecs WHERE statut = ? ORDER BY date_creation DESC";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT * FROM avecs WHERE phase_courante = ? ORDER BY date_creation DESC";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT * FROM avecs WHERE agent_villageois_id = ? ORDER BY date_creation DESC";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT * FROM avecs WHERE agent_terrain_id = ? ORDER BY date_creation DESC";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT * FROM avecs WHERE nom LIKE ? ORDER BY nom";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "UPDATE avecs SET phase_courante = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "UPDATE avecs SET statut = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "UPDATE avecs SET prochaine_reunion = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT COUNT(*) FROM avecs";

        try (Connection conn = dbConnection.getConnection();
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
        String sql = "SELECT COUNT(*) FROM avecs WHERE statut = ?";

        try (Connection conn = dbConnection.getConnection();
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
        avec.setCodeUnique(rs.getString("code_unique"));
        avec.setStatut(StatutAvec.valueOf(rs.getString("statut")));
        avec.setDateCreation(rs.getDate("date_creation").toLocalDate());
        avec.setNombreMembresMax(rs.getInt("nombre_membres_max"));
        avec.setPrixPart(rs.getBigDecimal("prix_part"));
        avec.setTauxFraisServiceMensuel(rs.getBigDecimal("taux_frais_service_mensuel"));
        avec.setPhaseCourante(PhaseCycle.valueOf(rs.getString("phase_courante")));

        Date dateDebutCycle = rs.getDate("date_debut_cycle");
        if (dateDebutCycle != null) {
            avec.setDateDebutCycle(dateDebutCycle.toLocalDate());
        }

        Date dateFinCyclePrevue = rs.getDate("date_fin_cycle_prevue");
        if (dateFinCyclePrevue != null) {
            avec.setDateFinCyclePrevue(dateFinCyclePrevue.toLocalDate());
        }

        avec.setLieuReunion(rs.getString("lieu_reunion"));

        String jourReunion = rs.getString("jour_reunion");
        if (jourReunion != null) {
            avec.setJourReunion(JourReunion.valueOf(jourReunion));
        }

        Time heureReunion = rs.getTime("heure_reunion");
        if (heureReunion != null) {
            avec.setHeureReunion(heureReunion.toLocalTime());
        }

        Date prochaineReunion = rs.getDate("prochaine_reunion");
        if (prochaineReunion != null) {
            avec.setProchaineReunion(prochaineReunion.toLocalDate());
        }

        avec.setCotisationCaisseSolidarite(rs.getBigDecimal("cotisation_caisse_solidarite"));
        avec.setCaisseSolidariteActive(rs.getBoolean("caisse_solidarite_active"));
        avec.setAgentVillageoisId(rs.getLong("agent_villageois_id"));

        long agentTerrainId = rs.getLong("agent_terrain_id");
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
        String sql = "SELECT * FROM avecs WHERE id = ?";

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