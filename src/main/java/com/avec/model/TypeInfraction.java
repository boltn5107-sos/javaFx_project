package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant un type d'infraction pour les amendes
 */
public class TypeInfraction {
    
    // Constantes pour les types d'infractions prédéfinis
    public static final String ABSENCE = "ABSENCE";
    public static final String RETARD = "RETARD";
    public static final String OUBLI_REGLE = "OUBLI_REGLE";
    public static final String PERTE_CARTE = "PERTE_CARTE";
    public static final String OUBLI_CLE = "OUBLI_CLE";
    public static final String BAVARDAGE = "BAVARDAGE";
    public static final String MANQUE_RESPECT = "MANQUE_RESPECT";
    public static final String OUBLI_SOLDE = "OUBLI_SOLDE";
    public static final String MANQUEMENT_COMITE = "MANQUEMENT_COMITE";
    
    private Long id;
    private String nom;
    private String description;
    private BigDecimal montantDefaut;
    private Boolean estActif;
    private LocalDateTime createdAt;
    
    public TypeInfraction() {
        this.estActif = true;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructeur avec paramètres principaux
     */
    public TypeInfraction(String nom, String description, BigDecimal montantDefaut) {
        this();
        this.nom = nom;
        this.description = description;
        this.montantDefaut = montantDefaut;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getMontantDefaut() {
        return montantDefaut;
    }
    
    public void setMontantDefaut(BigDecimal montantDefaut) {
        this.montantDefaut = montantDefaut;
    }
    
    public Boolean getEstActif() {
        return estActif;
    }
    
    public void setEstActif(Boolean estActif) {
        this.estActif = estActif;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Retourne le libellé en français du type d'infraction
     */
    public String getLibelle() {
        if (nom == null) return "";
        
        switch (nom) {
            case ABSENCE:
                return "Absence à une réunion";
            case RETARD:
                return "Retard à une réunion";
            case OUBLI_REGLE:
                return "Oubli des règles de l'Association";
            case PERTE_CARTE:
                return "Perte de la carte numérotée de membre";
            case OUBLI_CLE:
                return "Oubli de la clé";
            case BAVARDAGE:
                return "Bavardage pendant les délibérations";
            case MANQUE_RESPECT:
                return "Manque de respect vis-à-vis d'un autre membre";
            case OUBLI_SOLDE:
                return "Oubli des décisions ou des soldes de la réunion précédente";
            case MANQUEMENT_COMITE:
                return "Manquement aux obligations du Comité de Gestion";
            default:
                return nom;
        }
    }
    
    /**
     * Retourne le montant formaté
     */
    public String getMontantFormatted() {
        if (montantDefaut == null) return "0 FCFA";
        return String.format("%,.0f FCFA", montantDefaut).replace(',', ' ');
    }
    
    /**
     * Vérifie si le type d'infraction est actif
     */
    public boolean isActif() {
        return estActif != null && estActif;
    }
    
    /**
     * Crée une instance pour l'absence
     */
    public static TypeInfraction absence() {
        return new TypeInfraction(ABSENCE, "Absence à une réunion", new BigDecimal(500));
    }
    
    /**
     * Crée une instance pour le retard
     */
    public static TypeInfraction retard() {
        return new TypeInfraction(RETARD, "Retard à une réunion", new BigDecimal(200));
    }
    
    /**
     * Crée une instance pour l'oubli des règles
     */
    public static TypeInfraction oubliRegle() {
        return new TypeInfraction(OUBLI_REGLE, "Oubli des règles de l'Association", new BigDecimal(100));
    }
    
    /**
     * Crée une instance pour la perte de carte
     */
    public static TypeInfraction perteCarte() {
        return new TypeInfraction(PERTE_CARTE, "Perte de la carte numérotée de membre", new BigDecimal(250));
    }
    
    /**
     * Crée une instance pour l'oubli de clé
     */
    public static TypeInfraction oubliCle() {
        return new TypeInfraction(OUBLI_CLE, "Oubli de la clé", new BigDecimal(150));
    }
    
    /**
     * Crée une instance pour le bavardage
     */
    public static TypeInfraction bavardage() {
        return new TypeInfraction(BAVARDAGE, "Bavardage pendant les délibérations", new BigDecimal(100));
    }
    
    /**
     * Crée une instance pour le manque de respect
     */
    public static TypeInfraction manqueRespect() {
        return new TypeInfraction(MANQUE_RESPECT, "Manque de respect vis-à-vis d'un autre membre", new BigDecimal(300));
    }
    
    /**
     * Crée une instance pour l'oubli des soldes
     */
    public static TypeInfraction oubliSolde() {
        return new TypeInfraction(OUBLI_SOLDE, "Oubli des décisions ou des soldes de la réunion précédente", new BigDecimal(200));
    }
    
    /**
     * Crée une instance pour le manquement du comité
     */
    public static TypeInfraction manquementComite() {
        return new TypeInfraction(MANQUEMENT_COMITE, "Manquement aux obligations du Comité de Gestion", new BigDecimal(500));
    }
    
    /**
     * Récupère tous les types d'infractions prédéfinis
     */
    public static List<TypeInfraction> getPredefinedTypes() {
        List<TypeInfraction> types = new ArrayList<>();
        types.add(absence());
        types.add(retard());
        types.add(oubliRegle());
        types.add(perteCarte());
        types.add(oubliCle());
        types.add(bavardage());
        types.add(manqueRespect());
        types.add(oubliSolde());
        types.add(manquementComite());
        return types;
    }
    
    @Override
    public String toString() {
        return getLibelle() + " - " + getMontantFormatted();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeInfraction that = (TypeInfraction) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}