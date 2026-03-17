package com.avec.service;

import com.avec.dao.CycleDAO;
import com.avec.dao.AvecDAO;
import com.avec.dao.ReunionDAO;
import com.avec.enums.StatutCycle;
import com.avec.model.Cycle;
import com.avec.model.Avec;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CycleService {

    private final CycleDAO cycleDAO;
    private final AvecDAO avecDAO;
    private final ReunionDAO reunionDAO;

    public CycleService() {
        this.cycleDAO = new CycleDAO();
        this.avecDAO = new AvecDAO();
        this.reunionDAO = new ReunionDAO();
    }

    /**
     * Démarre un nouveau cycle pour une AVEC
     */
    public Cycle demarrerCycle(Long avecId) throws SQLException {
        // Vérifier que l'AVEC exist
        Avec avec = avecDAO.findAvecById(avecId);
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée avec l'ID: " + avecId);
        }

        // Vérifier qu'il n'y a pas de cycle en cours
        Cycle cycleEnCours = cycleDAO.findEnCoursByAvecId(avecId);
        if (cycleEnCours != null) {
            throw new IllegalStateException("Un cycle est déjà en cours pour cette AVEC");
        }

        // Déterminer le numéro du nouveau cycle
        List<Cycle> cyclesExistants = cycleDAO.findByAvecId(avecId);
        int nouveauNumero = cyclesExistants.size() + 1;

        // Créer le nouveau cycle
        Cycle nouveauCycle = new Cycle(LocalDate.now(), nouveauNumero, avecId);
        nouveauCycle.setStatut(StatutCycle.EN_COURS);

        return cycleDAO.insert(nouveauCycle);
    }

    /**
     * Clôture un cycle
     */
    public boolean cloturerCycle(Long cycleId, BigDecimal fondsFinal, int totalParts) throws SQLException {
        Cycle cycle = cycleDAO.findById(cycleId);
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle non trouvé avec l'ID: " + cycleId);
        }

        cycle.setFondsDeCreditFinal(fondsFinal);
        cycle.setTotalPartsAchetees(totalParts);
        cycle.cloturerCycle();

        return cycleDAO.update(cycle);
    }

    /**
     * Récupère un cycle par son ID
     */
    public Cycle getCycleById(long id) throws SQLException {
        return cycleDAO.findById(id);
    }

    /**
     * Récupère tous les cycles d'une AVEC
     */
    public List<Cycle> getCyclesByAvecId(long avecId) throws SQLException {
        return cycleDAO.findByAvecId(avecId);
    }

    /**
     * Récupère le cycle en cours d'une AVEC
     */
    public Cycle getCycleEnCours(long avecId) throws SQLException {
        return cycleDAO.findEnCoursByAvecId(avecId);
    }

    /**
     * Récupère tous les cycles
     */
    public List<Cycle> getAllCycles() throws SQLException {
        return cycleDAO.findAll();
    }
}