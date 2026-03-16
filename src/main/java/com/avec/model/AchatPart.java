package com.avec.model;

import java.math.BigDecimal;

public class AchatPart {

    private Long id;
    private int nombreParts;
    private BigDecimal montantTotal;

    private Membre membre;
    private Reunion reunion;

    public AchatPart(){}

    public AchatPart(Long id, int nombreParts, BigDecimal montantTotal) {
        this.id = id;
        this.nombreParts = nombreParts;
        this.montantTotal = montantTotal;
    }

    public Long getId() {
        return id;
    }

    public int getNombreParts() {
        return nombreParts;
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

}