package com.avec.service;

import com.avec.dao.VisiteDAO;
import com.avec.model.Visite;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class VisiteService {
    
    private final VisiteDAO visiteDAO;
    
    public VisiteService() {
        this.visiteDAO = new VisiteDAO();
    }
    
    /**
     * Enregistre une nouvelle visite
     */
    public boolean enregistrerVisite(Visite visite) {
        if (visite == null) return false;
        if (visite.getDate() == null) return false;
        if (visite.getAvecId() == null) return false;
        if (visite.getAgentVillageoisId() == null) return false;
        if (visite.getModule() == null || visite.getModule().isEmpty()) return false;
        
        return visiteDAO.enregistrer(visite);
    }
    
    /**
     * Met à jour une visite
     */
    public boolean modifierVisite(Visite visite) {
        if (visite == null || visite.getId() == null) return false;
        return visiteDAO.update(visite);
    }
    
    /**
     * Cherche une visite par ID
     */
    public Visite chercherVisiteParId(Long id) {
        if (id == null) return null;
        try {
            return visiteDAO.chercherId(id);
        } catch (SQLException e) {
            System.err.println("Erreur chercherVisiteParId: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Liste toutes les visites
     */
    public List<Visite> listerVisites() {
        try {
            return visiteDAO.lister();
        } catch (SQLException e) {
            System.err.println("Erreur listerVisites: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Liste les visites par AVEC
     */
    public List<Visite> listerVisitesParAvecId(Long avecId) {
        if (avecId == null) return List.of();
        try {
            return visiteDAO.listerParAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur listerVisitesParAvecId: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Liste les visites par agent villageois
     */
    public List<Visite> listerVisitesParAgentVillageoisId(Long agentId) {
        if (agentId == null) return List.of();
        try {
            return visiteDAO.listerParAgentVillageoisId(agentId);
        } catch (SQLException e) {
            System.err.println("Erreur listerVisitesParAgentVillageoisId: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Liste les visites par superviseur (agent terrain)
     */
    public List<Visite> listerVisitesParSuperviseurId(Long superviseurId) {
        if (superviseurId == null) return List.of();
        try {
            return visiteDAO.listerParSuperviseurId(superviseurId);
        } catch (SQLException e) {
            System.err.println("Erreur listerVisitesParSuperviseurId: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Liste les visites par module
     */
    public List<Visite> listerVisitesParModule(String module) {
        if (module == null || module.isEmpty()) return List.of();
        try {
            return visiteDAO.listerParModule(module);
        } catch (SQLException e) {
            System.err.println("Erreur listerVisitesParModule: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Liste les visites par période
     */
    public List<Visite> listerVisitesParPeriode(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) return List.of();
        try {
            return visiteDAO.listerParPeriode(debut, fin);
        } catch (SQLException e) {
            System.err.println("Erreur listerVisitesParPeriode: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Compte le nombre de visites pour une AVEC
     */
    public int countVisitesParAvecId(Long avecId) {
        if (avecId == null) return 0;
        try {
            return visiteDAO.countByAvecId(avecId);
        } catch (SQLException e) {
            System.err.println("Erreur countVisitesParAvecId: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Compte le nombre de visites pour un agent villageois
     */
    public int countVisitesParAgentVillageoisId(Long agentId) {
        if (agentId == null) return 0;
        try {
            return visiteDAO.countByAgentVillageoisId(agentId);
        } catch (SQLException e) {
            System.err.println("Erreur countVisitesParAgentVillageoisId: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Compte le nombre de visites pour un superviseur (agent terrain)
     */
    public int countVisitesByAgentTerrain(Long agentTerrainId) {
        if (agentTerrainId == null) return 0;
        try {
            return visiteDAO.countBySuperviseurId(agentTerrainId);
        } catch (SQLException e) {
            System.err.println("Erreur countVisitesByAgentTerrain: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Supprime une visite
     */
    public boolean supprimerVisite(Long id) {
        if (id == null) return false;
        try {
            return visiteDAO.supprimer(id);
        } catch (SQLException e) {
            System.err.println("Erreur supprimerVisite: " + e.getMessage());
            return false;
        }
    }
}