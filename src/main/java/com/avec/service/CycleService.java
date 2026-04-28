package com.avec.service;

import com.avec.dao.CycleDAO;
import com.avec.enums.StatutCycle;
import com.avec.model.Avec;
import com.avec.model.Cycle;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CycleService {

    private final CycleDAO cycleDAO;

    public CycleService() {
        this.cycleDAO = new CycleDAO();
    }

    /**
     * Crée le premier cycle (cycle de formation de 36 semaines)
     */
    public Cycle creerPremierCycle(Avec avec) throws SQLException {
        if (avec == null) {
            throw new IllegalArgumentException("L'AVEC ne peut pas être null");
        }
        
        // Vérifier qu'il n'y a pas déjà un cycle
        List<Cycle> cyclesExistants = cycleDAO.findByAvecId(avec.getId());
        if (!cyclesExistants.isEmpty()) {
            throw new IllegalStateException("Un cycle existe déjà pour cette AVEC");
        }
        
        Cycle cycle = new Cycle(LocalDate.now(), 1, avec.getId());
        cycle.setStatut(StatutCycle.EN_COURS);
        cycle.setFondsDeCreditFinal(BigDecimal.ZERO);
        cycle.setTotalPartsAchetees(0);
        
        return cycleDAO.insert(cycle);
    }
    
    /**
     * Crée un nouveau cycle après répartition
     */
    public Cycle creerNouveauCycle(Avec avec) throws SQLException {
        if (avec == null) {
            throw new IllegalArgumentException("L'AVEC ne peut pas être null");
        }
        
        // Trouver le dernier cycle pour connaître le numéro
        Cycle dernierCycle = cycleDAO.findLastCycleByAvecId(avec.getId());
        int nouveauNumero = (dernierCycle != null) ? dernierCycle.getNumeroCycle() + 1 : 1;
        
        Cycle cycle = new Cycle(LocalDate.now(), nouveauNumero, avec.getId());
        cycle.setStatut(StatutCycle.EN_COURS);
        cycle.setFondsDeCreditFinal(BigDecimal.ZERO);
        cycle.setTotalPartsAchetees(0);
        
        return cycleDAO.insert(cycle);
    }
    
    /**
     * Termine un cycle (module 7 - répartition du capital)
     */
    public Cycle terminerCycle(Long cycleId, BigDecimal fondsCreditFinal, int totalPartsAchetees) throws SQLException {
        Cycle cycle = cycleDAO.findById(cycleId);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle non trouvé");
        }
        
        if (cycle.getStatut() == StatutCycle.TERMINE) {
            throw new IllegalStateException("Ce cycle est déjà terminé");
        }
        
        cycle.setFondsDeCreditFinal(fondsCreditFinal);
        cycle.setTotalPartsAchetees(totalPartsAchetees);
        cycle.cloturerCycle(); // Utilise la méthode de votre modèle
        
        boolean updated = cycleDAO.terminerCycle(cycleId, cycle.getDateFinReelle(), 
                                                   fondsCreditFinal, totalPartsAchetees);
        
        if (updated) {
            return cycle;
        }
        return null;
    }
    
    /**
     * Récupère le cycle en cours d'une AVEC
     */
    public Cycle getCycleEnCours(Long avecId) throws SQLException {
        if (avecId == null) return null;
        return cycleDAO.findCycleEnCours(avecId);
    }
    
    /**
     * Récupère tous les cycles d'une AVEC
     */
    public List<Cycle> getCyclesByAvecId(Long avecId) throws SQLException {
        if (avecId == null) return List.of();
        return cycleDAO.findByAvecId(avecId);
    }
    
    /**
     * Récupère un cycle par son ID
     */
    public Cycle getCycleById(Long id) throws SQLException {
        if (id == null) return null;
        return cycleDAO.findById(id);
    }
    
    /**
     * Met à jour un cycle
     */
    public boolean updateCycle(Cycle cycle) throws SQLException {
        if (cycle == null || cycle.getId() == null) return false;
        return cycleDAO.update(cycle);
    }
    
    /**
     * Met à jour le statut d'un cycle
     */
    public boolean updateStatutCycle(Long cycleId, StatutCycle statut) throws SQLException {
        if (cycleId == null || statut == null) return false;
        return cycleDAO.updateStatut(cycleId, statut);
    }
    
    /**
     * Calcule le nombre total de cycles pour une AVEC
     */
    public int getNombreCycles(Long avecId) throws SQLException {
        if (avecId == null) return 0;
        return cycleDAO.countByAvecId(avecId);
    }
}