package com.avec.enums;

/**
 * Statut d'un membre dans l'AVEC
 */
public enum StatutMembre {
    ACTIF("Actif"),
    INACTIF("Inactif"),
    SUSPENDU("Suspendu"),
    RADIE("Radié");

    private final String libelle;

    StatutMembre(String libelle) {
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