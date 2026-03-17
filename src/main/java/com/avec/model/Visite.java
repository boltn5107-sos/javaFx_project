package com.avec.model;

import com.avec.enums.ModuleFormation;

import java.time.LocalDate;
import java.time.LocalTime;

public class Visite {

    private Long id;
    private Long avecId;
    private Avec avec;
    private Long agentVillageoisId;
    private AgentVillageois agentVillageois;
    private Long reunionId;
    private LocalDate date;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private ModuleFormation module;
    private int numeroVisite;
    private String observations;
    private boolean moduleComplete;
    private String recommandations;

    public Visite() {
    }

    // Getters et Setters

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

    public Long getReunionId() {
        return reunionId;
    }

    public void setReunionId(Long reunionId) {
        this.reunionId = reunionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(LocalTime heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public ModuleFormation getModule() {
        return module;
    }

    public void setModule(ModuleFormation module) {
        this.module = module;
    }

    public int getNumeroVisite() {
        return numeroVisite;
    }

    public void setNumeroVisite(int numeroVisite) {
        this.numeroVisite = numeroVisite;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public boolean isModuleComplete() {
        return moduleComplete;
    }

    public void setModuleComplete(boolean moduleComplete) {
        this.moduleComplete = moduleComplete;
    }

    public String getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(String recommandations) {
        this.recommandations = recommandations;
    }
}