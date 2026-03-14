package com.avec.model;

public class AgentTerrain extends Utilisateur {

	public AgentTerrain() {

		super();
	}

	public AgentTerrain(Utilisateur utilisateur) {

		super(utilisateur.getId(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(),
				utilisateur.getMotDePasse(), utilisateur.getTelephone());
	}

}
