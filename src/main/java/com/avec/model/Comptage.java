package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Comptage {

    private Long id;
    private Long avecId;
    private Avec avec;
    private LocalDate dateComptage;
    private BigDecimal fondCredit;
    private BigDecimal amendes;
    private BigDecimal total;
    private Long compteurId;
    private Membre compteur;
    private String observations;
    private boolean estConfirme;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comptage() {
        this.dateComptage = LocalDate.now();
        this.fondCredit = BigDecimal.ZERO;
        this.amendes = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.estConfirme = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAvecId() {
        return avecId;
    }

    public void setAvecId(Long avecId) {
        this.avecId = avecId;
    }

    public Avec getAvec() {
        return avec;
    }

    public void setAvec(Avec avec) {
        this.avec = avec;
        if (avec != null) {
            this.avecId = avec.getId();
        }
    }

    public LocalDate getDateComptage() {
        return dateComptage;
    }

    public void setDateComptage(LocalDate dateComptage) {
        this.dateComptage = dateComptage;
    }

    public BigDecimal getFondCredit() {
        return fondCredit;
    }

    public void setFondCredit(BigDecimal fondCredit) {
        this.fondCredit = fondCredit;
        calculerTotal();
    }

    public BigDecimal getAmendes() {
        return amendes;
    }

    public void setAmendes(BigDecimal amendes) {
        this.amendes = amendes;
        calculerTotal();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getCompteurId() {
        return compteurId;
    }

    public void setCompteurId(Long compteurId) {
        this.compteurId = compteurId;
    }

    public Membre getCompteur() {
        return compteur;
    }

    public void setCompteur(Membre compteur) {
        this.compteur = compteur;
        if (compteur != null) {
            this.compteurId = compteur.getId();
        }
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public boolean isEstConfirme() {
        return estConfirme;
    }

    public void setEstConfirme(boolean estConfirme) {
        this.estConfirme = estConfirme;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private void calculerTotal() {
        BigDecimal fc = fondCredit != null ? fondCredit : BigDecimal.ZERO;
        BigDecimal am = amendes != null ? amendes : BigDecimal.ZERO;
        this.total = fc.add(am);
    }

    public String getDateComptageFormatted() {
        return dateComptage != null ? dateComptage.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public String getTotalFormatted() {
        return total != null ? String.format("%,.0f", total).replace(',', ' ') + " FCA" : "0 FCA";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comptage that = (Comptage) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
