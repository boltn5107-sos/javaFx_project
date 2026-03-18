package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Remboursement {

	private Long id;
	private BigDecimal montant;
	private Long pretId;
	private Pret pret;
	private Reunion reunion;
	private Long reunionId;
	private LocalDateTime dateRemboursement;

	public Remboursement() {
		this.dateRemboursement = LocalDateTime.now();
	}

	public Remboursement(Long id, BigDecimal montant, Pret pret, Reunion reunion, LocalDateTime dateRemboursement) {

		this.id = id;
		this.montant = montant;
		this.pret = pret;
		if (pret != null) {
			this.pretId = pret.getId();
		}
		this.reunion = reunion;
		if (reunion != null) {
			this.reunionId = reunion.getId();
		}
		this.dateRemboursement = dateRemboursement;

	}

	// Getters et Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getMontant() {
		return montant;
	}

	public void setMontant(BigDecimal montant) {
		this.montant = montant;
	}

	public Long getPretId() {
		return pretId;
	}

	public void setPretId(Long pretId) {
		this.pretId = pretId;
	}

	public Pret getPret() {
		return pret;
	}

	public void setPret(Pret pret) {
		this.pret = pret;
		if (pret != null) {
			this.pretId = pret.getId();
		}
	}

	public Long getReunionId() {
		return reunionId;
	}

	public void setReunionId(Long reunionId) {
		this.reunionId = reunionId;
	}

	public Reunion getReunion() {
		return reunion;
	}

	public void setReunion(Reunion reunion) {
		this.reunion = reunion;
		if (reunion != null) {
			this.reunionId = reunion.getId();
		}
	}

	public LocalDateTime getDateRemboursement() {
		return dateRemboursement;
	}

	public void setDateRemboursement(LocalDateTime dateRemboursement) {
		this.dateRemboursement = dateRemboursement;
	}

	public String getDateRemboursementFormatted() {

		if (dateRemboursement != null) {
			return dateRemboursement.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		}

		return "";
	}

	/**
	 * Récupère le montant formaté avec le symbole FCFA
	 */
	public String getMontantFormatted() {
		if (montant != null) {
			return String.format("%,d FCFA", montant.longValue()).replace(',', ' ');
		}
		return "0 FCFA";
	}

	/**
	 * Vérifie si le remboursement est valide (montant > 0)
	 */
	public boolean estValide() {
		return montant != null && montant.compareTo(BigDecimal.ZERO) > 0;
	}

	@Override
	public String toString() {
		return "Remboursement{" + "id=" + id + ", montant=" + montant + ", pretId=" + pretId + ", dateRemboursement="
				+ dateRemboursement + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Remboursement that = (Remboursement) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}
