package com.avec.model;

public class Regle {

    private Long id;
    private Long avecId;
    private Avec avec;
    private String typeRegle;
    private String description;
    private String valeur;

    public Regle() {
    }

    public Regle(String typeRegle, String description, String valeur) {
        this.typeRegle = typeRegle;
        this.description = description;
        this.valeur = valeur;
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

    public String getTypeRegle() {
        return typeRegle;
    }

    public void setTypeRegle(String typeRegle) {
        this.typeRegle = typeRegle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}