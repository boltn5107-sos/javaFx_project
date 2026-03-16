package com.avec.service;

import com.avec.dao.AgentVillageoisDao;
import com.avec.model.AgentVillageois;
import java.util.List;

public class AgentVillageoisService {
    
    private AgentVillageoisDao agentVillageoisDao;
    
    public AgentVillageoisService() {
        this.agentVillageoisDao = new AgentVillageoisDao();
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
    
    public AgentVillageois chercherAvParId(Long id) {
        if (id == null) return null;
        return agentVillageoisDao.chercherId(id);
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