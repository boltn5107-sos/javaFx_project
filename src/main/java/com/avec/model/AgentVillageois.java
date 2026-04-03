package com.avec.model;

public class AgentVillageois extends Utilisateur {
    
    private AgentTerrain agentTerrain;
    private Long avecOrigineId;  // ✅ AVEC dont il était membre
    private Avec avecOrigine;     // ✅ AVEC d'origine
    
    public AgentVillageois() {
        super();
    }
    
    public AgentVillageois(Utilisateur utilisateur) {
        super(utilisateur.getId(), utilisateur.getNom(), utilisateur.getPrenom(), 
              utilisateur.getEmail(), utilisateur.getMotDePasse(), utilisateur.getTelephone());
    }
    
    public AgentTerrain getAgentTerrain() {
        return agentTerrain;
    }
    
    public void setAgentTerrain(AgentTerrain agentTerrain) {
        this.agentTerrain = agentTerrain;
    }
    
    public Long getAvecOrigineId() {
        return avecOrigineId;
    }
    
    public void setAvecOrigineId(Long avecOrigineId) {
        this.avecOrigineId = avecOrigineId;
    }
    
    public Avec getAvecOrigine() {
        return avecOrigine;
    }
    
    public void setAvecOrigine(Avec avecOrigine) {
        this.avecOrigine = avecOrigine;
        if (avecOrigine != null) {
            this.avecOrigineId = avecOrigine.getId();
        }
    }
}