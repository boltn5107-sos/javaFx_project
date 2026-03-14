package com.avec.model;

public class AgentVillageois extends Utilisateur {
	
	private AgentTerrain agentTerrain;
	public AgentVillageois() {
		super();
	}
	
	public AgentVillageois(Utilisateur utilisateur) {

		super(utilisateur.getId(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(),
				utilisateur.getMotDePasse(), utilisateur.getTelephone());
	}

	public AgentTerrain getAgentTerrain() {
		return agentTerrain;
	}
	
	public void setAgentTerrain(AgentTerrain agentTerrain) {
		
		this.agentTerrain = agentTerrain;
		
	}

}
