package com.avec.model;

public class AgentTerrain extends Utilisateur {
	
	private String zone;

	public AgentTerrain() {

		super();
	}

	public AgentTerrain(Utilisateur utilisateur) {

		super(utilisateur.getId(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(),
				utilisateur.getMotDePasse(), utilisateur.getTelephone());
	}
	
	 public String getZone() {
	        return zone;
	    }
	    
	    public void setZone(String zone) {
	        this.zone = zone;
	    }

}
