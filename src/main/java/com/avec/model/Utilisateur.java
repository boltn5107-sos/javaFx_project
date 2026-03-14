package com.avec.model;

public class Utilisateur {

	private Long id;
	private String nom;
	private String prenom;
	private String email;
	private String motDePasse;
	private String telephone;

	public Utilisateur() {
	}

	public Utilisateur(Long id, String nom, String prenom, String email, String motDepasse, String telephone) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.motDePasse = motDePasse;
		this.telephone = telephone;

	}

	// Getters et Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getNomComplet() {
		return prenom + " " + nom;
	}
	
	@Override
	public String toString() {
		return getNomComplet();
	}

}
