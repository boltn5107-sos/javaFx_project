package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Amende {
    private Long id;
    private Long membreId;
    private Membre membre;
    private Long reunionId;
    private Reunion reunion;
    private Long typeInfractionId;
    private TypeInfraction typeInfraction;
    private BigDecimal montant;
    private Boolean estPaye;
    private LocalDateTime datePaiement;
    private String observations;
    private LocalDateTime createdAt;
    
    public Amende() {
        this.estPaye = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMembreId() { return membreId; }
    public void setMembreId(Long membreId) { this.membreId = membreId; }
    
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { 
        this.membre = membre;
        if (membre != null) {
            this.membreId = membre.getId();
        }
    }
    
    public Long getReunionId() { return reunionId; }
    public void setReunionId(Long reunionId) { this.reunionId = reunionId; }
    
    public Reunion getReunion() { return reunion; }
    public void setReunion(Reunion reunion) { 
        this.reunion = reunion;
        if (reunion != null) {
            this.reunionId = reunion.getId();
        }
    }
    
    public Long getTypeInfractionId() { return typeInfractionId; }
    public void setTypeInfractionId(Long typeInfractionId) { this.typeInfractionId = typeInfractionId; }
    
    public TypeInfraction getTypeInfraction() { return typeInfraction; }
    public void setTypeInfraction(TypeInfraction typeInfraction) { 
        this.typeInfraction = typeInfraction;
        if (typeInfraction != null) {
            this.typeInfractionId = typeInfraction.getId();
            this.montant = typeInfraction.getMontantDefaut();
        }
    }
    
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    
    public Boolean getEstPaye() { return estPaye; }
    public void setEstPaye(Boolean estPaye) { this.estPaye = estPaye; }
    
    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getMontantFormatted() {
        if (montant == null) return "0 FCFA";
        return String.format("%,.0f FCFA", montant).replace(',', ' ');
    }
    
    public String getDatePaiementFormatted() {
        if (datePaiement != null) {
            return datePaiement.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "Non payé";
    }
    
    @Override
    public String toString() {
        return "Amende{" +
                "id=" + id +
                ", montant=" + montant +
                ", estPaye=" + estPaye +
                '}';

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amende that = (Amende) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
