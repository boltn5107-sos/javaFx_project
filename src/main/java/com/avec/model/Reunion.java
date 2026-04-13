package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.avec.enums.StatutReunion;
import com.avec.enums.TypeReunion;

public class Reunion {
    private Long id;
    private LocalDate date;
    private TypeReunion type;

    private List<AchatPart> achatParts;
    private List<Remboursement> remboursements;
    private List<DecaissementPret> decaissements;

    private StatutReunion statut;
    private Long cycleId;
    private Long avecId;
    private BigDecimal soldeFondCreditAvant;
    private BigDecimal soldesFondsCreditApres;
    private BigDecimal soldeCaisseSolidaritesApres;

    public Reunion() {
        this.statut = StatutReunion.PLANIFIEE;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public TypeReunion getType() { return type; }
    public void setType(TypeReunion type) { this.type = type; }

    public StatutReunion getStatut() { return statut; }
    public void setStatut(StatutReunion statut) { this.statut = statut; }

    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }

    public Long getAvecId() { return avecId; }
    public void setAvecId(Long avecId) { this.avecId = avecId; }

    public BigDecimal getSoldeFondCreditAvant() { return soldeFondCreditAvant; }
    public void setSoldeFondCreditAvant(BigDecimal soldeFondCreditAvant) { 
        this.soldeFondCreditAvant = soldeFondCreditAvant; 
    }

    public BigDecimal getSoldesFondsCreditApres() { return soldesFondsCreditApres; }
    public void setSoldesFondsCreditApres(BigDecimal soldesFondsCreditApres) { 
        this.soldesFondsCreditApres = soldesFondsCreditApres; 
    }

    public BigDecimal getSoldeCaisseSolidaritesApres() { return soldeCaisseSolidaritesApres; }
    public void setSoldeCaisseSolidaritesApres(BigDecimal soldeCaisseSolidaritesApres) { 
        this.soldeCaisseSolidaritesApres = soldeCaisseSolidaritesApres; 
    }

    public String getDateFormatted() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }

    public String getTypeLibelle() {
        return type != null ? type.toString() : "";
    }

    public String getStatutLibelle() {
        return statut != null ? statut.getLibelle() : "";
    }

    public boolean isEnCours() {
        return statut == StatutReunion.EN_COURS;
    }

    public boolean isTerminee() {
        return statut == StatutReunion.TERMINEE;
    }
    public List<AchatPart> getAchatParts() {
         return achatParts;
    }
    public void setAchatParts(List<AchatPart> achatParts) {
        this.achatParts = achatParts;
    }

   public List<Remboursement> getRemboursements() {
       return remboursements;
   }

   public void setRemboursements(List<Remboursement> remboursements) {
       this.remboursements = remboursements;
   }

   public List<DecaissementPret> getDecaissements() {
       return decaissements;
   }

   public void setDecaissements(List<DecaissementPret> decaissements) {
       this.decaissements = decaissements;
   }

    @Override
    public String toString() {
        if (date != null && type != null) {
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " + type.name();
        }
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
}

