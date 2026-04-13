package com.avec.service;

import com.avec.dao.AmendeDAO;
import com.avec.model.Amende;
import com.avec.model.Membre;
import com.avec.model.Reunion;
import com.avec.model.TypeInfraction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AmendeService {
    
    private AmendeDAO amendeDAO;
    
    public AmendeService() {
        this.amendeDAO = new AmendeDAO();
    }
    
    /**
     * Crée une nouvelle amende
     */
    public Amende creerAmende(Membre membre, Reunion reunion, TypeInfraction typeInfraction, 
                              BigDecimal montant, String observations) throws SQLException {
        if (membre == null || reunion == null || typeInfraction == null) {
            throw new IllegalArgumentException("Les informations obligatoires sont manquantes");
        }
        
        Amende amende = new Amende();
        amende.setMembre(membre);
        amende.setReunion(reunion);
        amende.setTypeInfraction(typeInfraction);
        amende.setMontant(montant != null ? montant : typeInfraction.getMontantDefaut());
        amende.setObservations(observations);
        amende.setEstPaye(false);
        
        return amendeDAO.insert(amende);
    }
    
    /**
     * Crée une amende avec le montant par défaut du type d'infraction
     */
    public Amende creerAmende(Membre membre, Reunion reunion, TypeInfraction typeInfraction, String observations) 
            throws SQLException {
        return creerAmende(membre, reunion, typeInfraction, null, observations);
    }
    
    /**
     * Marque une amende comme payée
     */
    public boolean marquerPayee(Long amendeId) throws SQLException {
        if (amendeId == null) return false;
        return amendeDAO.marquerPayee(amendeId);
    }
    
    /**
     * Récupère toutes les amendes d'un membre
     */
    public List<Amende> getAmendesByMembre(Long membreId) throws SQLException {
        if (membreId == null) return List.of();
        return amendeDAO.findByMembreId(membreId);
    }
    
    /**
     * Récupère toutes les amendes d'une réunion
     */
    public List<Amende> getAmendesByReunion(Long reunionId) throws SQLException {
        if (reunionId == null) return List.of();
        return amendeDAO.findByReunionId(reunionId);
    }
    
    /**
     * Récupère toutes les amendes non payées
     */
    public List<Amende> getAmendesNonPayees() throws SQLException {
        return amendeDAO.findNonPayees();
    }
    
    /**
     * Calcule le total des amendes non payées pour un membre
     */
    public BigDecimal getTotalNonPayeByMembre(Long membreId) throws SQLException {
        if (membreId == null) return BigDecimal.ZERO;
        return amendeDAO.getTotalNonPayeParMembre(membreId);
    }
    
    /**
     * Supprime une amende
     */
    public boolean supprimerAmende(Long id) throws SQLException {
        if (id == null) return false;
        return amendeDAO.delete(id);
    }
    
    /**
     * Crée une amende pour absence
     */
    public Amende creerAmendeAbsence(Membre membre, Reunion reunion, String observations) throws SQLException {
        TypeInfraction type = new TypeInfractionService().getTypeByNom(TypeInfraction.ABSENCE);
        return creerAmende(membre, reunion, type, observations);
    }
    
    /**
     * Crée une amende pour retard
     */
    public Amende creerAmendeRetard(Membre membre, Reunion reunion, String observations) throws SQLException {
        TypeInfraction type = new TypeInfractionService().getTypeByNom(TypeInfraction.RETARD);
        return creerAmende(membre, reunion, type, observations);
    }
}