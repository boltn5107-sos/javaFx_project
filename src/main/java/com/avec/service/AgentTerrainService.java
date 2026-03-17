package com.avec.service;

import com.avec.dao.AgentTerrainDao;
import com.avec.model.AgentTerrain;
import java.util.List;

public class AgentTerrainService {
    
    private AgentTerrainDao agentTerrainDao;
    
    public AgentTerrainService() {
        this.agentTerrainDao = new AgentTerrainDao();
    }
    
    // CRUD operations
    public boolean enregistrerAgentTerrain(AgentTerrain agent) {
        if (agent == null) return false;
        if (agent.getNom() == null || agent.getNom().trim().isEmpty()) return false;
        if (agent.getPrenom() == null || agent.getPrenom().trim().isEmpty()) return false;
        if (agent.getEmail() == null || agent.getEmail().trim().isEmpty()) return false;
        return agentTerrainDao.enregistrer(agent);
    }
    
    public AgentTerrain chercherAtParId(Long id) {
        if (id == null) return null;
        return agentTerrainDao.chercherId(id);
    }
    
    public List<AgentTerrain> listerAgentTerrain() {
        return agentTerrainDao.listerSimple();
    }
    
    public boolean modifierAgentTerrain(AgentTerrain agent) {
        if (agent == null || agent.getId() == null) return false;
        return agentTerrainDao.modifier(agent);
    }
    
    public boolean supprimerAgentTerrain(Long id) {
        if (id == null) return false;
        return agentTerrainDao.supprimer(id);
    }
    
    public int getNombreAgentTerrain() {
        return agentTerrainDao.compter();
    }
}