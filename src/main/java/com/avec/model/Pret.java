
package com.avec.model;

import java.math.BigDecimal;

import com.avec.enums.StatutPret;

public class Pret {

    private Long id;
    private String numeroPret;
    private BigDecimal montantInitial;
    private BigDecimal montantRestant;
    private StatutPret statut;
    private Membre emprunteur;

    public Pret(){}

    public Pret(Long id, String numeroPret, BigDecimal montantInitial) {
        this.id = id;
        this.numeroPret = numeroPret;
        this.montantInitial = montantInitial;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroPret() {
        return numeroPret;
    }

    public BigDecimal getMontantInitial() {
        return montantInitial;
    }

    public StatutPret getStatut() {
        return statut;
    }

    public void setStatut(StatutPret statut) {
        this.statut = statut;
    }

    public void setNumeroPret(String text) { this.numeroPret = numeroPret; }

    public void setMontantInitial(BigDecimal bigDecimal) { this.montantInitial = montantInitial; }
}