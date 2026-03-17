package com.avec.enums;

/**
 * Statuts possibles pour un décaissement de prêt
 */
public enum StatutDecaissement {
    EFFECTUE("Effectué"),
    ANNULE("Annulé"),
    EN_ATTENTE("En attente");

    private final String libelle;

    StatutDecaissement(String libelle) {
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