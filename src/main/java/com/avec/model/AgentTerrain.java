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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentTerrain that = (AgentTerrain) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
