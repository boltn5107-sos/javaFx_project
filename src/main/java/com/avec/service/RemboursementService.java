package com.avec.service;

import java.math.BigDecimal;
import java.util.List;

import com.avec.dao.RemboursementDAO;
import com.avec.model.Remboursement;

public class RemboursementService {

	private RemboursementDAO remboursementDao;

	public RemboursementService() {
		this.remboursementDao = new RemboursementDAO();
	}

	// Enregistrer un remboursement
	public boolean enregistreRemboursement(Remboursement remboursement) {
		if (remboursement == null)
			return false;
		if (remboursement.getMontant() == null || remboursement.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}

		if (remboursement.getPretId() == null || remboursement.getPret() == null) {
			return false;
		}
		if (remboursement.getReunionId() == null || remboursement.getReunion() == null) {
			return false;
		}

		// S'assurer que les IDs sont définis
		if (remboursement.getPret() != null && remboursement.getPretId() == null) {

			remboursement.setPretId(remboursement.getPret().getId());
		}

		if (remboursement.getReunion() != null && remboursement.getReunionId() == null) {

			remboursement.setPretId(remboursement.getReunion().getId());
		}

		return remboursementDao.enregistrer(remboursement);
	}
	
	// Chercher per ID
	public Remboursement chercherRemboursementParId(Long id) {
		if(id == null) return null;
		
		return remboursementDao.chercherId(id);
	}
	
	// Lister tous les remboursements
	public List<Remboursement> listerRemboursement(){
		return remboursementDao.lister();
	}
	
	// Lister par prêt
	public List<Remboursement> listerRemboursementsParPret(Long pretId){
		
		if(pretId == null) return null;
		
		return remboursementDao.listerParPretId(pretId);
	}

	// Lister par Réunion
		public List<Remboursement> listerRemboursementsParReunion(Long reunionId){
			
			if(reunionId == null) return null;
			
			return remboursementDao.listerParReunionId(reunionId);
		}
		
		// Supprimer un remboursement 
		public boolean supprimerRemboursement(Long id) {
			if(id == null) return false;
			
			return remboursementDao.supprimer(id);
		}
		
		// total des reùboursements pour un prêt
		public BigDecimal totalRemboursementsParPret(Long pretId) {
			if(pretId == null) return BigDecimal.ZERO;
			
			return remboursementDao.totalRemboursementsParPret(pretId);
		}
		
		// Compter le nombre de remboursements
		public int getNombreRemboursements() {
			return remboursementDao.compter();
		}
}
