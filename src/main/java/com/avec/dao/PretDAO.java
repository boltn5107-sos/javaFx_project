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
     * Enregistre un nouveau prêt (sans avecId)
     */
    public boolean enregistrer(Pret pret) {
        String sql = "INSERT INTO prets (numero_pret, montant_initial, frais_service_mensuel, " +
                     "montant_restant_du, duree_en_semaines, date_echeance, objet_pret, " +
                     "statut, emprunteur_id, reunion_decaissement_id, approuve_par_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            
            ps.setString(7, pret.getObjetPret());
            ps.setString(8, pret.getStatut().name());
            ps.setLong(9, pret.getEmprunteurId());
            ps.setLong(10, pret.getReunionDecaissementId());
            
            if (pret.getApprouveParId() != null) {
                ps.setLong(11, pret.getApprouveParId());
            } else {
                ps.setNull(11, Types.BIGINT);
            }

            int affectedRows = ps.executeUpdate();
            
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
        String sql = "UPDATE prets SET montant_restant_du = ?, statut = ?, date_remboursement_total = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, pret.getMontantRestantDu());
            ps.setString(2, pret.getStatut().name());
            
            if (pret.getDateRemboursementTotal() != null) {
                ps.setDate(3, Date.valueOf(pret.getDateRemboursementTotal()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            
            ps.setLong(4, pret.getId());

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
        String sql = "SELECT * FROM prets WHERE id = ?";

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
        List<Pret> prets = new ArrayList<>();
        String sql = "SELECT * FROM prets ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prets.add(mapResultSetToPret(rs));
            }
        }
        return prets;
    }

    /**
     * Liste les prêts par AVEC (en joignant avec la table membres)
     */
    public List<Pret> listerParAvecId(Long avecId) throws SQLException {
        List<Pret> prets = new ArrayList<>();
        // La requête utilise la table membres pour récupérer les prêts d'un AVEC
        String sql = "SELECT p.* FROM prets p " +
                     "INNER JOIN membres m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? ORDER BY p.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prets.add(mapResultSetToPret(rs));
                }
            }
        }
        return prets;
    }

    /**
     * Liste les prêts actifs (non remboursés)
     */
    public List<Pret> listerActifs() throws SQLException {
        List<Pret> prets = new ArrayList<>();
        String sql = "SELECT * FROM prets WHERE statut != 'REMBOURSE' ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prets.add(mapResultSetToPret(rs));
            }
        }
        return prets;
    }

    /**
     * Liste les prêts actifs par AVEC (en joignant avec la table membres)
     */
    public List<Pret> listerActifsParAvecId(Long avecId) throws SQLException {
        List<Pret> prets = new ArrayList<>();
        String sql = "SELECT p.* FROM prets p " +
                     "INNER JOIN membres m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut != 'REMBOURSE' ORDER BY p.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prets.add(mapResultSetToPret(rs));
                }
            }
        }
        return prets;
    }

    /**
     * Liste les prêts par emprunteur
     */
    public List<Pret> listerParEmprunteurId(Long emprunteurId) throws SQLException {
        List<Pret> prets = new ArrayList<>();
        String sql = "SELECT * FROM prets WHERE emprunteur_id = ? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, emprunteurId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prets.add(mapResultSetToPret(rs));
                }
            }
        }
        return prets;
    }

    /**
     * Liste les prêts en retard
     */
    public List<Pret> listerEnRetard() throws SQLException {
        List<Pret> prets = new ArrayList<>();
        String sql = "SELECT * FROM prets WHERE statut = 'ACTIF' AND date_echeance < CURDATE() ORDER BY date_echeance";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prets.add(mapResultSetToPret(rs));
            }
        }
        return prets;
    }

    /**
     * Compte le nombre total de prêts
     */
    public int compter() throws SQLException {
        String sql = "SELECT COUNT(*) FROM prets";

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
        String sql = "SELECT COUNT(*) FROM prets WHERE statut != 'REMBOURSE'";

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
    public int compterActifsParAvecId(Long avecId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prets p " +
                     "INNER JOIN membres m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut != 'REMBOURSE'";

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
     * Calcule le total des prêts décaissés
     */
    public BigDecimal totalDecaisse() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant_initial), 0) FROM prets";

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
    public BigDecimal totalDecaisseParAvecId(Long avecId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.montant_initial), 0) FROM prets p " +
                     "INNER JOIN membres m ON p.emprunteur_id = m.id " +
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

    /**
     * Calcule le total restant dû
     */
    public BigDecimal totalRestantDu() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant_restant_du), 0) FROM prets WHERE statut != 'REMBOURSE'";

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
    public BigDecimal totalRestantDuParAvecId(Long avecId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.montant_restant_du), 0) FROM prets p " +
                     "INNER JOIN membres m ON p.emprunteur_id = m.id " +
                     "WHERE m.avec_id = ? AND p.statut != 'REMBOURSE'";

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

    /**
     * Met à jour le statut d'un prêt
     */
    public boolean updateStatut(Long pretId, StatutPret statut) throws SQLException {
        String sql = "UPDATE prets SET statut = ? WHERE id = ?";

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
        String sql = "DELETE FROM prets WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Map un ResultSet vers un objet Pret
     */
    private Pret mapResultSetToPret(ResultSet rs) throws SQLException {
        Pret pret = new Pret();

        pret.setId(rs.getLong("id"));
        pret.setNumeroPret(rs.getString("numero_pret"));
        pret.setMontantInitial(rs.getBigDecimal("montant_initial"));
        pret.setFraisServiceMensuel(rs.getBigDecimal("frais_service_mensuel"));
        pret.setMontantRestantDu(rs.getBigDecimal("montant_restant_du"));
        pret.setDureeEnSemaines(rs.getInt("duree_en_semaines"));
        pret.setObjetPret(rs.getString("objet_pret"));
        pret.setStatut(StatutPret.valueOf(rs.getString("statut")));

        Date dateEcheance = rs.getDate("date_echeance");
        if (dateEcheance != null) {
            pret.setDateEcheance(dateEcheance.toLocalDate());
        }

        Date dateRemboursementTotal = rs.getDate("date_remboursement_total");
        if (dateRemboursementTotal != null) {
            pret.setDateRemboursementTotal(dateRemboursementTotal.toLocalDate());
        }

        pret.setEmprunteurId(rs.getLong("emprunteur_id"));
        pret.setReunionDecaissementId(rs.getLong("reunion_decaissement_id"));

        long approuveParId = rs.getLong("approuve_par_id");
        if (!rs.wasNull()) {
            pret.setApprouveParId(approuveParId);
        }

        return pret;
    }
}