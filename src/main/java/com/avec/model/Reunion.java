package com.avec.model;

import java.time.LocalDate;
import java.util.List;

import com.avec.enums.TypeReunion;

public class Reunion {

    private Long id;
    private LocalDate date;
    private TypeReunion type;
    private List<AchatPart> achatParts;
    private List<Remboursement> remboursements;
    private List<DecaissementPret> decaissements;

    public Reunion() {}

    public Reunion(Long id, LocalDate date, TypeReunion type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TypeReunion getType() {
        return type;
    }

    public void setType(TypeReunion type) {
        this.type = type;
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
}
