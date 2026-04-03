package com.avec.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.avec.dao.ReunionDAO;
import com.avec.enums.StatutReunion;
import com.avec.enums.TypeReunion;
import com.avec.model.Reunion;

public class ReunionService {

    private final ReunionDAO reunionDAO;

    public ReunionService() {
        this.reunionDAO = new ReunionDAO();
    }

    /**
     * Enregistre une nouvelle réunion
     */
    public boolean enregistrerReunion(Reunion reunion) {
        if (reunion == null) return false;
        if (reunion.getDate() == null) return false;
        if (reunion.getType() == null) return false;
        
        // Statut par défaut
        if (reunion.getStatut() == null) {
            reunion.setStatut(StatutReunion.PLANIFIEE);
        }
        
        return reunionDAO.enregistrer(reunion);
    }

    /**
     * Met à jour une réunion
     */
    public boolean modifierReunion(Reunion reunion) {
        if (reunion == null || reunion.getId() == null) return false;
        return reunionDAO.update(reunion);
    }

    /**
     * Cherche une réunion par son ID
     */
    public Reunion chercherReunionParId(Long id) {
        if (id == null) return null;
        try {
            return reunionDAO.chercherId(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la réunion: " + e.getMessage());
            return null;
        }
    }

    /**
     * Liste toutes les réunions
     */
    public List<Reunion> listerReunions() {
        try {
            return reunionDAO.lister();
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des réunions: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les réunions par cycle
     */
    public List<Reunion> listerReunionsParCycleId(Long cycleId) {
        if (cycleId == null) return List.of();
        try {
            return reunionDAO.listerParCycleId(cycleId);
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des réunions par cycle: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liste les réunions par type
     */
    public List<Reunion> listerReunionsParType(TypeReunion type) {
        if (type == null) return List.of();
        try {
            return reunionDAO.listerParType(type);
        } catch (SQLException e) {
            System.err.println("Erreur lors du listage des réunions par type: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Trouve la réunion en cours pour un cycle
     */
    public Reunion getReunionEnCours(Long cycleId) {
        if (cycleId == null) return null;
        try {
            return reunionDAO.findReunionEnCours(cycleId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la réunion en cours: " + e.getMessage());
            return null;
        }
    }

    /**
     * Trouve la prochaine réunion planifiée
     */
    public Reunion getProchaineReunion(Long cycleId) {
        if (cycleId == null) return null;
        try {
            return reunionDAO.findProchaineReunion(cycleId);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la prochaine réunion: " + e.getMessage());
            return null;
        }
    }

    /**
     * Démarre une réunion (passe le statut à EN_COURS)
     */
    public boolean demarrerReunion(Long reunionId) {
        if (reunionId == null) return false;
        try {
            return reunionDAO.updateStatut(reunionId, StatutReunion.EN_COURS);
        } catch (SQLException e) {
            System.err.println("Erreur lors du démarrage de la réunion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Termine une réunion (passe le statut à TERMINEE)
     */
    public boolean terminerReunion(Long reunionId, BigDecimal soldeFondCreditApres, 
                                    BigDecimal soldeCaisseSolidaritesApres) {
        if (reunionId == null) return false;
        try {
            boolean updated = reunionDAO.updateSoldes(reunionId, soldeFondCreditApres, soldeCaisseSolidaritesApres);
            if (updated) {
                return reunionDAO.updateStatut(reunionId, StatutReunion.TERMINEE);
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la clôture de la réunion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Annule une réunion
     */
    public boolean annulerReunion(Long reunionId) {
        if (reunionId == null) return false;
        try {
            return reunionDAO.updateStatut(reunionId, StatutReunion.ANNULEE);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'annulation de la réunion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime une réunion
     */
    public boolean supprimerReunion(Long id) {
        if (id == null) return false;
        try {
            return reunionDAO.supprimer(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réunion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Compte le nombre total de réunions
     */
    public int compterReunions() {
        try {
            return reunionDAO.compter();
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des réunions: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Crée une réunion d'épargne
     */
    public Reunion creerReunionEpargne(LocalDate date, Long cycleId) {
        Reunion reunion = new Reunion();
        reunion.setDate(date);
        reunion.setType(TypeReunion.EPARGNE);
        reunion.setCycleId(cycleId);
        reunion.setStatut(StatutReunion.PLANIFIEE);
        return reunion;
    }

    /**
     * Crée une réunion de crédit
     */
    public Reunion creerReunionCredit(LocalDate date, Long cycleId) {
        Reunion reunion = new Reunion();
        reunion.setDate(date);
        reunion.setType(TypeReunion.CREDIT);
        reunion.setCycleId(cycleId);
        reunion.setStatut(StatutReunion.PLANIFIEE);
        return reunion;
    }

    /**
     * Crée une réunion de répartition
     */
    public Reunion creerReunionRepartition(LocalDate date, Long cycleId) {
        Reunion reunion = new Reunion();
        reunion.setDate(date);
        reunion.setType(TypeReunion.REPARTITION);
        reunion.setCycleId(cycleId);
        reunion.setStatut(StatutReunion.PLANIFIEE);
        return reunion;
    }

    /**
     * Crée une réunion de formation
     */
    public Reunion creerReunionFormation(LocalDate date, Long cycleId) {
        Reunion reunion = new Reunion();
        reunion.setDate(date);
        reunion.setType(TypeReunion.FORMATION);
        reunion.setCycleId(cycleId);
        reunion.setStatut(StatutReunion.PLANIFIEE);
        return reunion;
    }
}