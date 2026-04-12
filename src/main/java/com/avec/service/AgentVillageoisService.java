package com.avec.service;

import java.sql.SQLException;
import java.util.List;

import com.avec.dao.AgentVillageoisDao;
import com.avec.enums.StatutAvec;
import com.avec.model.AgentVillageois;
import com.avec.model.Avec;

public class AgentVillageoisService {
    
    private AgentVillageoisDao agentVillageoisDao;
    private AvecService avecService;
    
    public AgentVillageoisService() {
        this.agentVillageoisDao = new AgentVillageoisDao();
        this.avecService = new AvecService();
    }
    
    // CRUD operations
    public boolean enregistrerAgentVillageois(AgentVillageois agent) {
        if (agent == null) return false;
        if (agent.getNom() == null || agent.getNom().trim().isEmpty()) return false;
        if (agent.getPrenom() == null || agent.getPrenom().trim().isEmpty()) return false;
        if (agent.getEmail() == null || agent.getEmail().trim().isEmpty()) return false;
        if (agent.getAgentTerrain() == null) return false;
        return agentVillageoisDao.enregistrer(agent);
    }
    
 // Dans AgentVillageoisService.java
    public boolean enregistrerAgentVillageoisParAt(AgentVillageois agent) {
        if (agent == null) return false;
        if (agent.getNom() == null || agent.getNom().trim().isEmpty()) return false;
        if (agent.getPrenom() == null || agent.getPrenom().trim().isEmpty()) return false;
        if (agent.getEmail() == null || agent.getEmail().trim().isEmpty()) return false;
        if (agent.getAgentTerrain() == null) return false;
        
        // ✅ Vérifier que le membre existe dans une AVEC terminée
        if (agent.getAvecOrigineId() != null) {
            try {
                Avec avec = avecService.getAvecById(agent.getAvecOrigineId());
                if (avec == null || avec.getStatut() != StatutAvec.TERMINE) {
                    return false; // L'AVEC doit être terminée
                }
            } catch (SQLException e) {
                return false;
            }
        }
        
        return agentVillageoisDao.enregistrerAVParAt(agent);
    }
    
    public AgentVillageois chercherAvParId(Long id) {
        if (id == null) return null;
        return agentVillageoisDao.chercherId(id);
    }
    
    /**
     * NOUVELLE MÉTHODE : Connexion par email et mot de passe
     */
    public AgentVillageois login(String email, String motDePasse) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            return null;
        }
        
        return agentVillageoisDao.chercherParEmailEtMotDePasse(email, motDePasse);
    }
    
    public List<AgentVillageois> listerAgentVillageois() {
        return agentVillageoisDao.lister();
    }
    
    public List<AgentVillageois> chercherAvParAt(Long agentTerrainId) {
        if (agentTerrainId == null) return null;
        return agentVillageoisDao.chercherParAt(agentTerrainId);
    }
    
    public boolean modifierAgentVillageois(AgentVillageois agent) {
        if (agent == null || agent.getId() == null) return false;
        return agentVillageoisDao.modifier(agent);
    }
    
    public boolean supprimerAgentVillageois(Long id) {
        if (id == null) return false;
        return agentVillageoisDao.supprimer(id);
    }
    
    public int getNombreAgentVillageois() {
        return agentVillageoisDao.compter();
    }
}