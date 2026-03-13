package com.avec.enums;

/**
 * Statuts possibles pour une AVEC
 */

 public enum StatutAvec{
    EN_FORMATION("En formation"),
    ACTVIE("Active"),
    EN_PAUSE("En pause"),
    TERMINE("Termine"),
    EN_DISSOLUTION("En dissolution");

    private final String libelle;

    StatutAvec(String libelle){
        this.libelle= libelle;
    }

    public String getLibelle(){
        return libelle;
    }
    @Override
    public String toString(){
        return libelle;
    }

 }