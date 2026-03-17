package com.avec.service;

import com.avec.dao.DecaissementPretDAO;
import com.avec.dao.PretDAO;
import com.avec.dao.ReunionDAO;
import com.avec.dao.MembreDAO;
import com.avec.model.DecaissementPret;
import com.avec.model.Pret;
import com.avec.model.Reunion;
import com.avec.model.Membre;
import com.avec.enums.StatutPret;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion des décaissements de prêts
 */
public class DecaissementPretService {

    private final DecaissementPretDAO decaissementDAO;
    private final PretDAO pretDAO;
    private final ReunionDAO reunionDAO;
    private final MembreDAO membreDAO;

    public DecaissementPretService() {
        this.decaissementDAO = new DecaissementPretDAO();
        this.pretDAO = new PretDAO();
        this.reunionDAO = new ReunionDAO();
        this.membreDAO = new MembreDAO();
    }

    /**
     * Enregistre un nouveau décaissement
     */
//    public DecaissementPret enregistrerDecaissement(Long pretId, Long reunionId, BigDecimal montant,
//                                                    String modePaiement, String observations,
//                                                    Long encaisseParId, Long approuveParId)
//            throws SQLException, IllegalArgumentException {
//
//        // Vérifier que le prêt existe
//        Pret pret = pretDAO.findById(pretId);
//        if (pret == null) {
//            throw new IllegalArgumentException("Prêt non trouvé avec l'ID: " + pretId);
//        }
//
//        // Vérifier que le prêt est actif
//        if (pret.getStatut() != StatutPret.ACTIF) {
//            throw new IllegalStateException("Le prêt n'est pas actif");
//        }
//
//        // Vérifier que le montant ne dépasse pas le montant restant dû
//        if (montant.compareTo(pret.getMontantRestantDu()) > 0) {
//            throw new IllegalArgumentException("Le montant du décaissement (" + montant +
//                    ") dépasse le montant restant dû (" +
//                    pret.getMontantRestantDu() + ")");
//        }
//
//        // Vérifier que la réunion existe
//        Reunion reunion = reunionDAO.findById(reunionId);
//        if (reunion == null) {
//            throw new IllegalArgumentException("Réunion non trouvée avec l'ID: " + reunionId);
//        }
//
//        // Créer le décaissement
//        DecaissementPret decaissement = new DecaissementPret(montant, pretId, reunionId);
//        decaissement.setModePaiement(modePaiement);
//        decaissement.setObservations(observations);
//        decaissement.setEncaisseParId(encaisseParId);
//        decaissement.setApprouveParId(approuveParId);
//
//        // Mettre à jour le montant restant dû du prêt
//        BigDecimal nouveauMontantRestant = pret.getMontantRestantDu().subtract(montant);
//        pret.setMontantRestantDu(nouveauMontantRestant);
//
//        // Si le prêt est entièrement remboursé, mettre à jour son statut
//        if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) == 0) {
//            pret.setStatut(StatutPret.REMBOURSE);
//        }
//
//        // Sauvegarder dans une transaction
//        try {
//            // Insérer le décaissement
//            DecaissementPret saved = decaissementDAO.insert(decaissement);
//
//            // Mettre à jour le prêt
//            pretDAO.update(pret);
//
//            return saved;
//
//        } catch (SQLException e) {
//            throw new SQLException("Erreur lors de l'enregistrement du décaissement: " + e.getMessage());
//        }
//    }

    /**
     * Récupère un décaissement par son ID
     */
    public DecaissementPret getDecaissementById(long id) throws SQLException {
        return decaissementDAO.findById(id);
    }

    /**
     * Récupère tous les décaissements d'un prêt
     */
    public List<DecaissementPret> getDecaissementsByPretId(long pretId) throws SQLException {
        return decaissementDAO.findByPretId(pretId);
    }

    /**
     * Récupère tous les décaissements d'une réunion
     */
    public List<DecaissementPret> getDecaissementsByReunionId(long reunionId) throws SQLException {
        return decaissementDAO.findByReunionId(reunionId);
    }

    /**
     * Récupère les décaissements par période
     */
    public List<DecaissementPret> getDecaissementsByPeriode(LocalDateTime debut, LocalDateTime fin) throws SQLException {
        return decaissementDAO.findByPeriode(debut, fin);
    }

    /**
     * Calcule le total des décaissements pour un prêt
     */
    public BigDecimal getTotalDecaissementsByPretId(long pretId) throws SQLException {
        return decaissementDAO.sumMontantByPretId(pretId);
    }

    /**
     * Annule un décaissement
     */
    public boolean annulerDecaissement(long decaissementId) throws SQLException {
        DecaissementPret decaissement = decaissementDAO.findById(decaissementId);
        if (decaissement == null) {
            throw new IllegalArgumentException("Décaissement non trouvé");
        }

        // Récupérer le prêt associé
        Pret pret = pretDAO.findPretById(decaissement.getPretId());
        if (pret == null) {
            throw new IllegalStateException("Prêt associé non trouvé");
        }

        // Restaurer le montant dans le prêt
        BigDecimal nouveauMontantRestant = pret.getMontantTotalDu().add(decaissement.getMontant());
        pret.setMontantRestantDu(nouveauMontantRestant);

        // Remettre le prêt en ACTIF si nécessaire
        if (pret.getStatut() == StatutPret.REMBOURSE) {
            pret.setStatut(StatutPret.ACTIF);
        }

//        // Mettre à jour le prêt
//        pretDAO.update(pret);

        // Supprimer le décaissement
        return decaissementDAO.delete(decaissementId);
    }

    /**
     * Récupère les détails complets d'un décaissement (avec objets associés)
     */
//    public DecaissementPret getDecaissementWithDetails(long id) throws SQLException {
//        DecaissementPret decaissement = decaissementDAO.findById(id);
//        if (decaissement != null) {
//            // Charger le prêt
//            Pret pret = pretDAO.findById(decaissement.getPretId());
//            decaissement.setPret(pret);
//
//            // Charger la réunion
//            Reunion reunion = reunionDAO.findById(decaissement.getReunionId());
//            decaissement.setReunion(reunion);
//
//            // Charger l'encaisseur
//            if (decaissement.getEncaisseParId() != null) {
//                Membre encaisseur = membreDAO.findById(decaissement.getEncaisseParId());
//                decaissement.setEncaissePar(encaisseur);
//            }
//
//            // Charger l'approbateur
//            if (decaissement.getApprouveParId() != null) {
//                Membre approbateur = membreDAO.findById(decaissement.getApprouveParId());
//                decaissement.setApprouvePar(approbateur);
//            }
//        }
//        return decaissement;
//    }

    /**
     * Valide qu'un décaissement peut être effectué
     */
    public boolean peutDecaisser(Pret pret, BigDecimal montant) throws SQLException {
        if (pret == null) return false;
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) return false;
        if (pret.getStatut() != StatutPret.ACTIF) return false;

        // Vérifier que le montant ne dépasse pas le montant restant dû
        return montant.compareTo(pret.getMontantTotalDu()) <= 0;
    }
}