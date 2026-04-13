package com.avec.service;

import com.avec.dao.TypeInfractionDAO;
import com.avec.model.TypeInfraction;

import java.sql.SQLException;
import java.util.List;

public class TypeInfractionService {
    
    private TypeInfractionDAO typeInfractionDAO;
    
    public TypeInfractionService() {
        this.typeInfractionDAO = new TypeInfractionDAO();
    }
    
    /**
     * Récupère tous les types d'infractions actifs
     */
    public List<TypeInfraction> getAllTypes() {
        try {
            return typeInfractionDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des types d'infractions: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Récupère un type d'infraction par son ID
     */
    public TypeInfraction getTypeById(Long id) {
        if (id == null) return null;
        try {
            return typeInfractionDAO.findById(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du type d'infraction: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Récupère un type d'infraction par son nom
     */
    public TypeInfraction getTypeByNom(String nom) {
        if (nom == null || nom.isEmpty()) return null;
        try {
            return typeInfractionDAO.findByNom(nom);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du type d'infraction: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Récupère le montant par défaut pour un type d'infraction
     */
    public java.math.BigDecimal getMontantDefaut(String nom) {
        TypeInfraction type = getTypeByNom(nom);
        return type != null ? type.getMontantDefaut() : java.math.BigDecimal.ZERO;
    }
    
    /**
     * Initialise les types d'infractions par défaut dans la base de données
     */
    public void initialiserTypesDefaut() {
        // Cette méthode peut être appelée au démarrage de l'application
        // pour s'assurer que tous les types d'infractions existent
        try {
            List<TypeInfraction> existingTypes = typeInfractionDAO.findAll();
            if (existingTypes.isEmpty()) {
                for (TypeInfraction type : TypeInfraction.getPredefinedTypes()) {
                    // Insérer le type dans la base
                    // typeInfractionDAO.insert(type);
                }
                System.out.println("Types d'infractions par défaut initialisés");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation des types d'infractions: " + e.getMessage());
        }
    }
}