package com.avec.enums;

public enum StatutPret {
    EN_ATTENTE("En attente"),
    ACTIF("Actif"),
    REMBOURSE("Remboursé"),
    EN_RETARD("En retard"),
    REJET("Rejetee"),
    IMPAYE("Impayé");

    private final String libelle;

    StatutPret(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}