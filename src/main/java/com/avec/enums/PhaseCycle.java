package com.avec.enums;

/**
 * phases du sycle de formation d'un AVEC
 */

    public enum PhaseCycle {
        PREPERATIORE("preparatiore", 0, "Reunoin d'information " ),
        INTENSIVE("Intensive",12, " 4 visites semaine 1 , puis 6 visites "),
        DEVELOPPEMENT("Developpement", 24 , "3 vistes de supervision"),
        MATURITE("Maturite", 36 ,"2 visite , preparation repartition"),
        TERMINE("Termine", 0 ,"Cycle termine" );

        private final String libelle;
        private final int dureeSemaines;
        private final String description;



    PhaseCycle(String libelle, int dureeSemaines, String description ) {
        this.libelle = libelle;
        this.dureeSemaines = dureeSemaines;
        this.description = description;

    }

    public String getLibelle() {
        return libelle;
    }
    public int getDureeSemaines() {
        return dureeSemaines;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return libelle;
    }
}