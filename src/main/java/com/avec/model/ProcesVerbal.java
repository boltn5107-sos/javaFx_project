package com.avec.model;

import java.time.LocalDateTime;

public class ProcesVerbal {

    private Long id;
    private Long reunionId;
    private Reunion reunion;
    private String contenu;
    private String decisions;
    private String observations;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private Long creeParId;
    private Membre creePar;

    public ProcesVerbal() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getDecisions() {
        return decisions;
    }

    public void setDecisions(String decisions) {
        this.decisions = decisions;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Long getCreeParId() {
        return creeParId;
    }

    public void setCreeParId(Long creeParId) {
        this.creeParId = creeParId;
    }

    public Membre getCreePar() {
        return creePar;
    }

    public void setCreePar(Membre creePar) {
        this.creePar = creePar;
        if (creePar != null) {
            this.creeParId = creePar.getId();
        }
    }

    public String getDateCreationFormatted() {
        return dateCreation != null ? dateCreation.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }

    public String getDateModificationFormatted() {
        return dateModification != null ? dateModification.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcesVerbal that = (ProcesVerbal) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
