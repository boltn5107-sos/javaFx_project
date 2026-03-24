package com.avec.model;

import com.avec.enums.RoleComite;

public class SessionUtilisateur {

	private static SessionUtilisateur instance;
	private Utilisateur utilisateur;
	private Membre membre;
	private AgentVillageois agentVillageois;
	private AgentTerrain agentTerrain;
	private String typeUtilisateur;
	private RoleComite roleComite;

	private SessionUtilisateur() {
	}

	public static SessionUtilisateur getInstance() {
		if (instance == null) {
			instance = new SessionUtilisateur();
		}

		return instance;
	}

	public void connecterAdmin(Utilisateur user) {
		this.utilisateur = user;
		this.membre = null;
		this.agentVillageois = null;
		this.agentTerrain = null;
		this.typeUtilisateur = "ADMIN";
		this.roleComite = null;
	}

	public void connecterMembre(Membre membre) {
		this.membre = membre;
		this.utilisateur = null;
		this.agentVillageois = null;
		this.agentTerrain = null;
		this.typeUtilisateur = "MEMBRE";
		this.roleComite = membre.getRoleComite();
	}

	public void connecterAgentVillageois(AgentVillageois agent) {
		this.agentVillageois = agent;
		this.utilisateur = agent;
		this.membre = null;
		this.agentTerrain = null;
		this.typeUtilisateur = "AGENT_VILLAGEOIS";
		this.roleComite = null;
	}

	public void connecterAgentTerrain(AgentTerrain agent) {
		this.agentTerrain = agent;
		this.utilisateur = agent;
		this.membre = null;
		this.agentVillageois = null;
		this.typeUtilisateur = "AGENT_TERRAIN";
		this.roleComite = null;
	}

	public void deconnecter() {
		this.agentTerrain = null;
		this.utilisateur = null;
		this.membre = null;
		this.agentVillageois = null;
		this.typeUtilisateur = null;
		this.roleComite = null;
	}

	public boolean estConnecte() {
		return utilisateur != null || membre != null || agentVillageois != null || agentTerrain != null;
	}

	public String getNomUtilisateur() {
		if (utilisateur != null)
			return utilisateur.getPrenom() + " " + utilisateur.getNom();
		if (membre != null)
			return membre.getPrenom() + " " + membre.getNom();
		if (agentVillageois != null)
			return agentVillageois.getPrenom() + " " + agentVillageois.getNom();
		if (agentTerrain != null)
			return agentTerrain.getPrenom() + " " + agentTerrain.getNom();

		return "Inconnu";
	}

	public String getTypeUtilisateurr() {
		return typeUtilisateur;
	}

	public boolean isAdmin() {
		return "ADMIN".equals(typeUtilisateur);
	}

	public boolean isAgentTerrain() {
		return "AGENT_TERRAIN".equals(typeUtilisateur);
	}

	public boolean isAgentVillageois() {
		return "AGENT_VILLAGEOIS".equals(typeUtilisateur);
	}

	public boolean isMembre() {
		return "MEMBRE".equals(typeUtilisateur);
	}

	public boolean isPresident() {
		return isMembre() && roleComite == RoleComite.PRESIDENT;
	}

	public boolean isSecretaire() {
		return isMembre() && roleComite == RoleComite.SECRETAIRE;
	}

	public boolean isTresorier() {
		return isMembre() && roleComite == RoleComite.TRESORIER;
	}

	public boolean isCompteur() {
		return isMembre() && roleComite == RoleComite.COMPTEUR;
	}

	public boolean isMembreSimple() {
		return isMembre() && roleComite == RoleComite.AUCUN;
	}

	public RoleComite geRoleComite() {
		return roleComite;
	}

	public String getRoleLibelle() {
		if (isAdmin())
			return "Administrateur";
		if (isAgentTerrain())
			return "Agent de Terrain";
		if (isAgentVillageois())
			return "Agent Villageois";
		if (isMembre() && roleComite != null)
			return roleComite.getDescription();
		return "Utilisateur";
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public Membre getMembre() {
		return membre;
	}

	public AgentVillageois getAgentVillageois() {
		return agentVillageois;
	}

	public AgentTerrain getAgentTerrain() {
		return agentTerrain;
	}

	public Long getId() {

		if(utilisateur != null) return utilisateur.getId();
		if(membre != null) return membre.getId();
		if(agentTerrain != null) return agentTerrain.getId();
		if(agentVillageois != null) return agentVillageois.getId();
		
		return null;
	}

}
