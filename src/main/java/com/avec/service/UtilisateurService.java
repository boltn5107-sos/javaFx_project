package com.avec.service;

import java.util.List;

import com.avec.dao.UtilisateurDao;
import com.avec.model.Utilisateur;

public class UtilisateurService {

	private UtilisateurDao utilisateurDao;

	public UtilisateurService() {
		this.utilisateurDao = new UtilisateurDao();

	}

	// Connexion

	public Utilisateur login(String email, String password) {

		if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return null;
		}
		return utilisateurDao.chercherEmailEtMotDePasse(email, password);
	}

	// CRUD opérations

	public boolean AjouterUtilisateur(Utilisateur utilisateur) {

		if (utilisateur == null)
			return false;

		return utilisateurDao.ajouter(utilisateur);
	}

	public Utilisateur chercherUtilisateur(Long id) {

		if (id == null)
			return null;

		return utilisateurDao.chercherId(id);
	}

	public List<Utilisateur> listerUtilisateur() {
		return utilisateurDao.lister();
	}

	public boolean modifierUtilisateur(Utilisateur utilisateur) {

		if (utilisateur == null || utilisateur.getId() == null)
			return false;

		return utilisateurDao.modifier(utilisateur);
	}

	public boolean supprimerUtilisateur(Long id) {
		if (id == null)
			return false;

		return utilisateurDao.spprimer(id);
	}

	public int getNombreUtilisateurs() {
		return utilisateurDao.getNombreUtilisateur();
	}

	public boolean changerMotDePasse(Long userId, String ancienMotDePasse, String nouveauMotDePasse,
			String confirmationMotDePasse) {
		// Vérifications
		if (userId == null) {
			System.err.println("ID utilisateur null");
			return false;
		}

		if (ancienMotDePasse == null || ancienMotDePasse.trim().isEmpty()) {
			System.err.println("Ancien mot de passe requis");
			return false;
		}

		if (nouveauMotDePasse == null || nouveauMotDePasse.trim().isEmpty()) {
			System.err.println("Nouveau mot de passe requis");
			return false;
		}

		if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
			System.err.println("Les nouveaux mots de passe ne correspondent pas");
			return false;
		}

		if (nouveauMotDePasse.length() < 4) {
			System.err.println("Le mot de passe doit contenir au moins 4 caractères");
			return false;
		}

// Vérifier l'ancien mot de passe
		Utilisateur utilisateur = utilisateurDao.chercherId(userId);
		if (utilisateur == null) {
			System.err.println("Utilisateur non trouvé");
			return false;
		}

		if (!utilisateur.getMotDePasse().equals(ancienMotDePasse)) {
			System.err.println("Ancien mot de passe incorrect");
			return false;
		}

// Mettre à jour le mot de passe
		utilisateur.setMotDePasse(nouveauMotDePasse);
		return utilisateurDao.modifier(utilisateur);
	}

}
