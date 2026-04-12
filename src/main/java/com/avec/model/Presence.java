package com.avec.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Presence {
    private Long id;
    private Long membreId;
    private Membre membre;
    private Long reunionId;
    private Reunion reunion;
    private Boolean estPresent;
    private Boolean estRetard;
    private LocalTime heureArrivee;
    private String motifAbsence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Presence() {
        this.estPresent = true;
        this.estRetard = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public Boolean getEstPresent() { return estPresent; }
    public void setEstPresent(Boolean estPresent) { this.estPresent = estPresent; }
    
    public Boolean getEstRetard() { return estRetard; }
    public void setEstRetard(Boolean estRetard) { this.estRetard = estRetard; }
    
    public LocalTime getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(LocalTime heureArrivee) { this.heureArrivee = heureArrivee; }
    
    public String getMotifAbsence() { return motifAbsence; }
    public void setMotifAbsence(String motifAbsence) { this.motifAbsence = motifAbsence; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getHeureArriveeFormatted() {
        if (heureArrivee != null) {
            return heureArrivee.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }
    
    public String getStatut() {
        if (!estPresent) return "Absent";
        if (estRetard) return "En retard";
        return "Présent";
    }
    
    @Override
    public String toString() {
        return "Presence{" +
                "id=" + id +
                ", statut=" + getStatut() +
                '}';
    }
}