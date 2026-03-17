package com.avec.enums;

/**
 * Rôles pour les détenteurs des clés de la caisse
 */
public enum RoleDetenteurCle {
    GARDIEN_CLE_1("Gardien clé 1"),
    GARDIEN_CLE_2("Gardien clé 2"),
    GARDIEN_CLE_3("Gardien clé 3"),
    AUCUN("Non détenteur");

    private final String libelle;

    RoleDetenteurCle(String libelle) {
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