package com.avec.enums;

public enum ModuleFormation {
    MODULE_1("Module 1", "Groupes, leadership et élections"),
    MODULE_2("Module 2", "Règlements de la Caisse de Solidarité, achat de parts et prêts"),
    MODULE_3("Module 3", "Élaboration du règlement intérieur"),
    MODULE_4("Module 4", "Première réunion d'épargne"),
    MODULE_5("Module 5", "Première réunion de crédit"),
    MODULE_6("Module 6", "Premier remboursement de crédit"),
    MODULE_7("Module 7", "Répartition du capital, élections et indépendance");

    private final String code;
    private final String description;

    ModuleFormation(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}