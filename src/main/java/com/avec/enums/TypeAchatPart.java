package com.avec.enums;

public enum TypeAchatPart  {



        ACHAT("Achat de parts"),
        VENTE("Vente de parts");

        private final String libelle;

        TypeAchatPart(String libelle) {
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

