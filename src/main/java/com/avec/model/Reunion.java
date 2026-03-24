package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.avec.enums.StatutReunion;
import com.avec.enums.TypeReunion;

public class Reunion {
    private Long id;
    private LocalDate date;
    private TypeReunion type;
    private StatutReunion statut;
    private Long cycleId;
    private BigDecimal soldeFondCreditAvant;
    private BigDecimal soldesFondsCreditApres;
    private BigDecimal soldeCaisseSolidaritesApres;

    public Reunion() {
        this.statut = StatutReunion.PLANIFIEE;
        this.soldeFondCreditAvant = BigDecimal.ZERO;
        this.soldesFondsCreditApres = BigDecimal.ZERO;
        this.soldeCaisseSolidaritesApres = BigDecimal.ZERO;
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

    @Override
    public String toString() {
        return type != null ? type.describeConstable() + " - " + getDateFormatted() : "";
    }
}