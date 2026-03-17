package com.avec.model;

import com.avec.enums.StatutCycle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un cycle annuel d'une AVEC
 */
public class Cycle {

    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private LocalDate dateFinReelle;
    private StatutCycle statut;
    private BigDecimal fondsDeCreditFinal;
    private Integer totalPartsAchetees;
    private BigDecimal valeurPart;
    private int numeroCycle;

    // Relations
    private Long avecId;
    private Avec avec;

    private List<Reunion> reunions;

    /**
     * Constructeur par défaut
     */
    public Cycle() {
        this.statut = StatutCycle.PREVU;
        this.reunions = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres principaux
     */
    public Cycle(LocalDate dateDebut, int numeroCycle, Long avecId) {
        this();
        this.dateDebut = dateDebut;
        this.dateFinPrevue = dateDebut.plusWeeks(36);
        this.numeroCycle = numeroCycle;
        this.avecId = avecId;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
        if (dateDebut != null && this.dateFinPrevue == null) {
            this.dateFinPrevue = dateDebut.plusWeeks(36);
        }
    }

    public LocalDate getDateFinPrevue() {
        return dateFinPrevue;
    }

    public void setDateFinPrevue(LocalDate dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
    }

    public LocalDate getDateFinReelle() {
        return dateFinReelle;
    }

    public void setDateFinReelle(LocalDate dateFinReelle) {
        this.dateFinReelle = dateFinReelle;
    }

    public StatutCycle getStatut() {
        return statut;
    }

    public void setStatut(StatutCycle statut) {
        this.statut = statut;
    }

    public BigDecimal getFondsDeCreditFinal() {
        return fondsDeCreditFinal;
    }

    public void setFondsDeCreditFinal(BigDecimal fondsDeCreditFinal) {
        this.fondsDeCreditFinal = fondsDeCreditFinal;
    }

    public Integer getTotalPartsAchetees() {
        return totalPartsAchetees;
    }

    public void setTotalPartsAchetees(Integer totalPartsAchetees) {
        this.totalPartsAchetees = totalPartsAchetees;
    }

    public BigDecimal getValeurPart() {
        return valeurPart;
    }

    public void setValeurPart(BigDecimal valeurPart) {
        this.valeurPart = valeurPart;
    }

    public int getNumeroCycle() {
        return numeroCycle;
    }

    public void setNumeroCycle(int numeroCycle) {
        this.numeroCycle = numeroCycle;
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

    public List<Reunion> getReunions() {
        return reunions;
    }

    public void setReunions(List<Reunion> reunions) {
        this.reunions = reunions;
    }

    // Méthodes métier

    /**
     * Calcule la valeur de la part en fin de cycle
     */
    public BigDecimal calculerValeurPart() {
        if (fondsDeCreditFinal == null || totalPartsAchetees == null || totalPartsAchetees == 0) {
            return BigDecimal.ZERO;
        }
        return fondsDeCreditFinal.divide(BigDecimal.valueOf(totalPartsAchetees), 2, RoundingMode.HALF_UP);
    }

    /**
     * Clôture le cycle
     */
    public void cloturerCycle() {
        this.dateFinReelle = LocalDate.now();
        this.statut = StatutCycle.TERMINE;
        this.valeurPart = calculerValeurPart();
    }

    /**
     * Récupère le nombre de réunions effectuées
     */
    public int getNombreReunionsEffectuees() {
        return reunions != null ? reunions.size() : 0;
    }

    /**
     * Vérifie si le cycle est conforme (durée minimale)
     */
    public boolean estConforme() {
        return getNombreReunionsEffectuees() >= 36;
    }

    /**
     * Calcule la durée effective du cycle en semaines
     */
    public long getDureeEnSemaines() {
        if (dateDebut == null) return 0;
        LocalDate fin = dateFinReelle != null ? dateFinReelle : LocalDate.now();
        return java.time.temporal.ChronoUnit.WEEKS.between(dateDebut, fin);
    }

    @Override
    public String toString() {
        return "Cycle " + numeroCycle + " (" + dateDebut + " - " +
                (dateFinReelle != null ? dateFinReelle : "en cours") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cycle cycle = (Cycle) o;
        return id != null && id.equals(cycle.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}