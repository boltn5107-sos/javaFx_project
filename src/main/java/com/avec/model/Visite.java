package com.avec.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Visite {

    private Long id;
    private LocalDateTime date;
    private String module;
    private String observations;
    private boolean moduleComplete; 
    private int numeroVisite;
    
    // Relations
    private Long avecId;
    private Avec avec;
    private Long agentVillageoisId;
    private AgentVillageois agentVillageois;
    private Long superviseurPresentId;
    private AgentTerrain superviseurPresent;

    public Visite() {
        this.date = LocalDateTime.now();
    }
    
    
    public boolean isModuleComplete() {  // ✅ Méthode getter pour boolean
        return moduleComplete;
    }
    
    public void setModuleComplete(boolean moduleComplete) {
        this.moduleComplete = moduleComplete;
    }
    
    public int getNumeroVisite() {
        return numeroVisite;
    }
    
    public void setNumeroVisite(int numeroVisite) {
        this.numeroVisite = numeroVisite;
    }
    

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
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

    public Long getAgentVillageoisId() {
        return agentVillageoisId;
    }

    public void setAgentVillageoisId(Long agentVillageoisId) {
        this.agentVillageoisId = agentVillageoisId;
    }

    public AgentVillageois getAgentVillageois() {
        return agentVillageois;
    }

    public void setAgentVillageois(AgentVillageois agentVillageois) {
        this.agentVillageois = agentVillageois;
        if (agentVillageois != null) {
            this.agentVillageoisId = agentVillageois.getId();
        }
    }

    public Long getSuperviseurPresentId() {
        return superviseurPresentId;
    }

    public void setSuperviseurPresentId(Long superviseurPresentId) {
        this.superviseurPresentId = superviseurPresentId;
    }

    public AgentTerrain getSuperviseurPresent() {
        return superviseurPresent;
    }

    public void setSuperviseurPresent(AgentTerrain superviseurPresent) {
        this.superviseurPresent = superviseurPresent;
        if (superviseurPresent != null) {
            this.superviseurPresentId = superviseurPresent.getId();
        }
    }

    public String getDateFormatted() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }

    @Override
    public String toString() {
        return "Visite{" +
                "id=" + id +
                ", date=" + date +
                ", module='" + module + '\'' +
                '}';
    }
}