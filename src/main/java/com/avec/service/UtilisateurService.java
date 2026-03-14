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
		
		if(email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return null;
		}
		return utilisateurDao.chercherEmailEtMotDePasse(email, password);
	}
	
	//CRUD opérations
	
	public boolean AjouterUtilisateur(Utilisateur utilisateur) {
		
		if (utilisateur == null) return false;
		
		return utilisateurDao.ajouter(utilisateur);
	}
	
	public Utilisateur chercherUtilisateur(Long id) {
		
		if(id == null) return null;
		
		return utilisateurDao.chercherId(id);
	}
	
	public List<Utilisateur> listerUtilisateur(){
		return utilisateurDao.lister();
	}
	
	public boolean modifierUtilisateur(Utilisateur utilisateur) {
		
		if(utilisateur == null || utilisateur.getId() == null) return false;
		
		return utilisateurDao.modifier(utilisateur);
	}
	
	public boolean supprimerUtilisateur(Long id) {
		if(id == null) return false;
		
		return utilisateurDao.spprimer(id);
	}
}
