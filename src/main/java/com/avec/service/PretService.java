package com.avec.service;

import com.avec.dao.PretDAO;
import com.avec.enums.StatutPret;
import com.avec.model.Pret;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PretService {

    private final PretDAO pretDAO;

    public PretService() {
        this.pretDAO = new PretDAO();
    }

    /**
     * Enregistre un nouveau prêt
     */
    public boolean enregistrerPret(Pret pret) {
        if (pret == null) return false;
        if (pret.getMontantInitial() == null || pret.getMontantInitial().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (pret.getEmprunteurId() == null) {
            return false;
        }
        if (pret.getReunionDecaissementId() == null) {
            return false;
        }

        // Générer un numéro de prêt unique
        if (pret.getNumeroPret() == null || pret.getNumeroPret().isEmpty()) {
            pret.setNumeroPret(genererNumeroPret());
        }

        // Calculer le montant restant dû (principal + intérêts)
        BigDecimal montantTotal = pret.getMontantTotalDu();
        pret.setMontantRestantDu(montantTotal);

        // Définir la date d'échéance
        if (pret.getDateEcheance() == null && pret.getReunionDecaissement() != null) {
            LocalDate dateDecaissement = pret.getReunionDecaissement().getDate();
            if (dateDecaissement != null) {
                pret.setDateEcheance(dateDecaissement.plusWeeks(pret.getDureeEnSemaines()));
            }
        }

        // Statut par défaut
        if (pret.getStatut() == null) {
            pret.setStatut(StatutPret.ACTIF);
        }

        return pretDAO.enregistrer(pret);
    }

    /**
     * Met à jour un prêt
     */
    public boolean modifierPret(Pret pret) {
        if (pret == null || pret.getId() == null) return false;
        return pretDAO.update(pret);
    }

    /**
     * Cherche un prêt par son ID
     */
    public Pret chercherPretParId(Long id) {
        if (id == null) return null;
        try {
            return pretDAO.chercherId(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du prêt: " + e.getMessage());
            return null;
        }
    }

    /**
     * Liste tous les prêts
     */
    public List<Pret> listerPrets() {
        try {
            return pretDAO.lister();
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des prêts: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les prêts par AVEC (utilise la jointure avec membres)
     */
    public List<Pret> listerPretsParAvecId(Long avecId) {
        if (avecId == null) return List.of();
        try {
            return pretDAO.listerParAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des prêts par AVEC: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les prêts actifs (non remboursés)
     */
    public List<Pret> listerPretsActifs() {
        try {
            return pretDAO.listerActifs();
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des prêts actifs: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les prêts actifs par AVEC
     */
    public List<Pret> listerPretsActifsParAvecId(Long avecId) {
        if (avecId == null) return List.of();
        try {
            return pretDAO.listerActifsParAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des prêts actifs par AVEC: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les prêts par emprunteur
     */
    public List<Pret> listerPretsParEmprunteurId(Long emprunteurId) {
        if (emprunteurId == null) return List.of();
        try {
            return pretDAO.listerParEmprunteurId(emprunteurId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des prêts par emprunteur: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les prêts en retard
     */
    public List<Pret> listerPretsEnRetard() {
        try {
            return pretDAO.listerEnRetard();
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des prêts en retard: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Compte le nombre total de prêts
     */
    public int compterPrets() {
        try {
            return pretDAO.compter();
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des prêts: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Compte le nombre de prêts actifs
     */
    public int compterPretsActifs() {
        try {
            return pretDAO.compterActifs();
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des prêts actifs: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Compte le nombre de prêts actifs par AVEC
     */
    public int compterPretsActifsParAvecId(Long avecId) {
        if (avecId == null) return 0;
        try {
            return pretDAO.compterActifsParAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des prêts actifs par AVEC: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Calcule le total des prêts décaissés
     */
    public BigDecimal totalDecaisse() {
        try {
            return pretDAO.totalDecaisse();
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total décaissé: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calcule le total des prêts décaissés par AVEC
     */
    public BigDecimal totalDecaisseParAvecId(Long avecId) {
        if (avecId == null) return BigDecimal.ZERO;
        try {
            return pretDAO.totalDecaisseParAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total décaissé par AVEC: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calcule le total restant dû
     */
    public BigDecimal totalRestantDu() {
        try {
            return pretDAO.totalRestantDu();
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total restant dû: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calcule le total restant dû par AVEC
     */
    public BigDecimal totalRestantDuParAvecId(Long avecId) {
        if (avecId == null) return BigDecimal.ZERO;
        try {
            return pretDAO.totalRestantDuParAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total restant dû par AVEC: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Met à jour le statut d'un prêt
     */
    public boolean updateStatutPret(Long pretId, StatutPret statut) {
        if (pretId == null || statut == null) return false;
        try {
            return pretDAO.updateStatut(pretId, statut);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un prêt
     */
    public boolean supprimerPret(Long id) {
        if (id == null) return false;
        try {
            return pretDAO.supprimer(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du prêt: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un membre peut emprunter
     */
    public boolean peutEmprunter(Long membreId, Long avecId) {
        try {
            List<Pret> pretsActifs = pretDAO.listerActifsParAvecId(avecId);
            for (Pret pret : pretsActifs) {
                if (pret.getEmprunteurId().equals(membreId)) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification d'éligibilité: " + e.getMessage());
            return false;
        }
    }

    /**
     * Génère un numéro de prêt unique
     */
    private String genererNumeroPret() {
        String prefix = "PRT";
        String date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String unique = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + "-" + date + "-" + unique;
    }
}