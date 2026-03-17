package com.avec.enums;

/**
 * Statuts possibles pour un cycle
 */
public enum StatutCycle {
    PREVU("Prévu"),
    EN_COURS("En cours"),
    TERMINE("Terminé");

    private final String libelle;

    StatutCycle(String libelle) {
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