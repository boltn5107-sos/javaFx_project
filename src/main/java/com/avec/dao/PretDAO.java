package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.enums.StatutPret;
import com.avec.model.Pret;

import java.sql.*;

public class PretDAO {

    public void enregistrer(Pret pret) {

        String sql = "INSERT INTO pret(numeroPret,montantInitial) VALUES(?,?)";

        try {

            DBConnection db = new DBConnection();
            Connection con = db.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, pret.getNumeroPret());
            ps.setBigDecimal(2, pret.getMontantInitial());

            ps.executeUpdate();

            System.out.println("Pret enregistré");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public Pret findPretById(Long id) throws SQLException {
        String sql = "SELECT * FROM prets WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pret pret = new Pret();

                    pret.setId(rs.getLong("id"));
                    pret.setNumeroPret(rs.getString("numero_pret"));
                    pret.setMontantInitial(rs.getBigDecimal("montant_initial"));
                    pret.setFraisServiceMensuel(rs.getBigDecimal("frais_service_mensuel"));
                    pret.setMontantRestantDu(rs.getBigDecimal("montant_restant_du"));
                    pret.setDureeEnSemaines(rs.getInt("duree_en_semaines"));

                    Date dateEcheance = rs.getDate("date_echeance");
                    if (dateEcheance != null) {
                        pret.setDateEcheance(dateEcheance.toLocalDate());
                    }

                    pret.setObjetPret(rs.getString("objet_pret"));
                    pret.setStatut(StatutPret.valueOf(rs.getString("statut")));

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
        }
        return null;
    }
}