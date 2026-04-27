package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.StatutPret;
import com.avec.model.Pret;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PretDAO {

    /**
     * Enregistre un nouveau prêt
     */
    public boolean enregistrer(Pret pret) {
        String sql = "INSERT INTO pret (numeroPret, montantInitial, fraisServiceMensuel, " +
                     "montantRestantDu, dureeMaxEnSemaines, dateEcheance, " +
                     "statut, emprunteur_id, reunionDecaissement_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pret.getNumeroPret());
            ps.setBigDecimal(2, pret.getMontantInitial());
            ps.setBigDecimal(3, pret.getFraisServiceMensuel());
            ps.setBigDecimal(4, pret.getMontantRestantDu());
            ps.setInt(5, pret.getDureeEnSemaines());
            
            if (pret.getDateEcheance() != null) {
                ps.setDate(6, Date.valueOf(pret.getDateEcheance()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            
            ps.setString(7, pret.getStatut().name());
            ps.setLong(8, pret.getEmprunteurId());
            
            if (pret.getReunionDecaissementId() != null) {
                ps.setLong(9, pret.getReunionDecaissementId());
            } else {
                ps.setNull(9, Types.BIGINT);
            }

            System.out.println("Exécution de l'INSERT pret...");
            int affectedRows = ps.executeUpdate();
            System.out.println("Lignes affectées: " + affectedRows);
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pret.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            
            return false;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement du prêt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour un prêt existant
     */
    public boolean update(Pret pret) {
         String sql = "UPDATE pret SET montantRestantDu = ?, statut = ?, dateEcheance = ?, reunionDecaissement_id = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, pret.getMontantRestantDu());
            ps.setString(2, pret.getStatut().name());
            
            if (pret.getDateEcheance() != null) {
                ps.setDate(3, Date.valueOf(pret.getDateEcheance()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            
            if (pret.getReunionDecaissementId() != null) {
                ps.setLong(4, pret.getReunionDecaissementId());
            } else {
                ps.setNull(4, Types.BIGINT);
            }
            
            ps.setLong(5, pret.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du prêt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cherche un prêt par son ID
     */
    public Pret chercherId(Long id) throws SQLException {
        String sql = "SELECT * FROM pret WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPret(rs);
                }
            }
        }
        return null;
    }

    /**
     * Liste tous les prêts
     */
    public List<Pret> lister() throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT * FROM pret ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pret.add(mapResultSetToPret(rs));
            }
        }
        return pret;
    }

    /**
     * Liste les prêts par AVEC (en joignant avec la table membres)
     */
    public List<Pret> listerParAvecId(Long avec_id) throws SQLException {
        List<Pret> pret = new ArrayList<>();
        // La requête utilise la table membres pour récupérer les prêts d'un AVEC
        String sql = "SELECT p.* FROM pret p " +
                     "INNER JOIN membre m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? ORDER BY p.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avec_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pret.add(mapResultSetToPret(rs));
                }
            }
        }
        return pret;
    }

    /**
     * Liste les prêts actifs (non remboursés)
     */
    public List<Pret> listerActifs() throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT * FROM pret WHERE statut != 'REMBOURSE' ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pret.add(mapResultSetToPret(rs));
            }
        }
        return pret;
    }

    /**
     * Liste les prêts actifs par AVEC (en joignant avec la table membres)
     */
    public List<Pret> listerActifsParAvecId(Long avec_id) throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT p.* FROM pret p " +
                     "INNER JOIN membre m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut != 'REMBOURSE' ORDER BY p.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avec_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pret.add(mapResultSetToPret(rs));
                }
            }
        }
        return pret;
    }

    /**
     * Liste les prêts par emprunteur
     */
    public List<Pret> listerParEmprunteurId(Long emprunteur_id) throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT * FROM pret WHERE emprunteur_id = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, emprunteur_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pret.add(mapResultSetToPret(rs));
                }
            }
        }
        return pret;
    }

    /**
     * Liste les prêts en retard
     */
    public List<Pret> listerEnRetard() throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT * FROM pret WHERE statut = 'ACTIF' AND dateEcheance < CURDATE() ORDER BY dateEcheance";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pret.add(mapResultSetToPret(rs));
            }
        }
        return pret;
    }

    /**
     * Liste les prêts par statut
     */
    public List<Pret> listerParStatut(StatutPret statut) throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT * FROM pret WHERE statut = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pret.add(mapResultSetToPret(rs));
                }
            }
        }
        return pret;
    }

    /**
     * Liste les demandes en attente par AVEC
     */
    public List<Pret> listerEnAttenteParAvecId(Long avec_id) throws SQLException {
        List<Pret> pret = new ArrayList<>();
        String sql = "SELECT p.* FROM pret p " +
                     "INNER JOIN membre m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut = 'EN_ATTENTE' ORDER BY p.id DESC";
        
        System.out.println(">>> SQL: " + sql);
        System.out.println(">>> avec_id: " + avec_id);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avec_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pret.add(mapResultSetToPret(rs));
                }
            }
        }
        System.out.println(">>> Nombre de prêts trouvés: " + pret.size());
        return pret;
    }

    /**
     * Compte le nombre total de prêts
     */
    public int compter() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pret";

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
     * Compte le nombre de prêts actifs
     */
    public int compterActifs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pret WHERE statut != 'REMBOURSE'";

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
     * Compte le nombre de prêts actifs par AVEC
     */
    public int compterActifsParAvecId(Long avec_id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pret p " +
                     "INNER JOIN membre m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut != 'REMBOURSE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avec_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Calcule le total des prêts décaissés
     */
    public BigDecimal totalDecaisse() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montantInitial), 0) FROM pret";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcule le total des prêts décaissés par AVEC
     */
    public BigDecimal totalDecaisseParAvecId(Long avec_id) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.montantInitial), 0) FROM pret p " +
                     "INNER JOIN membre m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avec_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcule le total restant dû
     */
    public BigDecimal totalRestantDu() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant_restant_du), 0) FROM pret WHERE statut != 'REMBOURSE'";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcule le total restant dû par AVEC
     */
    public BigDecimal totalRestantDuParAvecId(Long avec_id) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.montant_restant_du), 0) FROM pret p " +
                     "INNER JOIN membre m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut != 'REMBOURSE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avec_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Met à jour le statut d'un prêt
     */
    public boolean updateStatut(Long pretId, StatutPret statut) throws SQLException {
        String sql = "UPDATE pret SET statut = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            stmt.setLong(2, pretId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime un prêt
     */
    public boolean supprimer(Long id) throws SQLException {
        String sql = "DELETE FROM pret WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Trouve les prêts par ID emprunteur
     */
    public List<Pret> findByEmprunteurId(Long emprunteurId) throws SQLException {
        return listerParEmprunteurId(emprunteurId);
    }

    /**
     * Map un ResultSet vers un objet Pret
     */
    private Pret mapResultSetToPret(ResultSet rs) throws SQLException {
        Pret pret = new Pret();

        pret.setId(rs.getLong("id"));
        pret.setNumeroPret(rs.getString("numeroPret"));
        pret.setMontantInitial(rs.getBigDecimal("montantInitial"));
        pret.setFraisServiceMensuel(rs.getBigDecimal("fraisServiceMensuel"));
        pret.setMontantRestantDu(rs.getBigDecimal("montantRestantDu"));
        pret.setDureeEnSemaines(rs.getInt("dureeMaxEnSemaines"));

        pret.setStatut(StatutPret.valueOf(rs.getString("statut")));

        Date dateEcheance = rs.getDate("dateEcheance");
        if (dateEcheance != null) {
            pret.setDateEcheance(dateEcheance.toLocalDate());
        }

        pret.setEmprunteurId(rs.getLong("emprunteur_id"));
        
        long reunionId = rs.getLong("reunionDecaissement_id");
        if (!rs.wasNull()) {
            pret.setReunionDecaissementId(reunionId);
        }

        return pret;
    }
}