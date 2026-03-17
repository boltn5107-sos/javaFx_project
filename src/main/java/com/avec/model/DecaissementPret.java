package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Classe représentant un décaissement de prêt dans une AVEC
 * Correspond au moment où l'argent est effectivement remis à l'emprunteur
 */
public class DecaissementPret {

    private Long id;
    private String numeroDecaissement;
    private BigDecimal montant;
    private LocalDateTime dateDecaissement;
    private String modePaiement; // Espèces, Mobile Money, etc.
    private String observations;

    // Relations
    private Long pretId;
    private Pret pret;

    private Long reunionId;
    private Reunion reunion;

    private Long encaisseParId; // L'agent ou le membre qui a remis l'argent
    private Membre encaissePar;

    private Long approuveParId; // Membre qui a approuvé le décaissement
    private Membre approuvePar;

    /**
     * Constructeur par défaut
     */
    public DecaissementPret() {
        this.dateDecaissement = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres principaux
     */
    public DecaissementPret(BigDecimal montant, Long pretId, Long reunionId) {
        this();
        this.montant = montant;
        this.pretId = pretId;
        this.reunionId = reunionId;
        genererNumeroDecaissement();
    }

    /**
     * Génère un numéro de décaissement unique
     */
    private void genererNumeroDecaissement() {
        String prefix = "DEC";
        String date = String.valueOf(java.time.LocalDate.now().getYear());
        String random = String.valueOf((int) (Math.random() * 10000));
        this.numeroDecaissement = String.format("%s-%s-%04d", prefix, date, Integer.parseInt(random));
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDecaissement() {
        return numeroDecaissement;
    }

    public void setNumeroDecaissement(String numeroDecaissement) {
        this.numeroDecaissement = numeroDecaissement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du décaissement doit être positif");
        }
        this.montant = montant;
    }

    public LocalDateTime getDateDecaissement() {
        return dateDecaissement;
    }

    public void setDateDecaissement(LocalDateTime dateDecaissement) {
        this.dateDecaissement = dateDecaissement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
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

    public Long getEncaisseParId() {
        return encaisseParId;
    }

    public void setEncaisseParId(Long encaisseParId) {
        this.encaisseParId = encaisseParId;
    }

    public Membre getEncaissePar() {
        return encaissePar;
    }

    public void setEncaissePar(Membre encaissePar) {
        this.encaissePar = encaissePar;
        if (encaissePar != null) {
            this.encaisseParId = encaissePar.getId();
        }
    }

    public Long getApprouveParId() {
        return approuveParId;
    }

    public void setApprouveParId(Long approuveParId) {
        this.approuveParId = approuveParId;
    }

    public Membre getApprouvePar() {
        return approuvePar;
    }

    public void setApprouvePar(Membre approuvePar) {
        this.approuvePar = approuvePar;
        if (approuvePar != null) {
            this.approuveParId = approuvePar.getId();
        }
    }

    @Override
    public String toString() {
        return "DecaissementPret{" +
                "numeroDecaissement='" + numeroDecaissement + '\'' +
                ", montant=" + montant +
                ", dateDecaissement=" + dateDecaissement +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecaissementPret that = (DecaissementPret) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}