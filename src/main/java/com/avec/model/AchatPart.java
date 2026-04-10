package com.avec.model;

import java.math.BigDecimal;

public class AchatPart {

    private Long id;
    private int nombreParts;
    private BigDecimal montantTotal;

    private Membre membre;
    private Reunion reunion;
    private Long reunion_id;
    private Long membre_id;

    public AchatPart(){}

    public AchatPart(Long id, int nombreParts, BigDecimal montantTotal) {
        this.id = id;
        this.nombreParts = nombreParts;
        this.montantTotal = montantTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNombreParts() {
        return nombreParts;
    }

    public void setNombreParts(int nombreParts) {
        this.nombreParts = nombreParts;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Reunion getReunion() {
        return reunion;
    }

    public void setReunion(Reunion reunion) {
        this.reunion = reunion;
    }

	public Long getReunionId() {
		return reunion_id;
	}

	public void setReunionId(Long reunion_id) {
		this.reunion_id = reunion_id;
	}

	public Long getMembreId() {
		return membre_id;
	}

	public void setMembreId(Long membre_id) {
		this.membre_id = membre_id;
	}
    
}