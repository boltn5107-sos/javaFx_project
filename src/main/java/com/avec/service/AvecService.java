package com.avec.service;

import com.avec.dao.AvecDAO;
import com.avec.dao.AgentVillageoisDao;
import com.avec.dao.AgentTerrainDao;
import com.avec.dao.MembreDAO;
import com.avec.enums.PhaseCycle;
import com.avec.enums.StatutAvec;
import com.avec.enums.JourReunion;
import com.avec.enums.StatutMembre;
import com.avec.model.Avec;
import com.avec.model.AgentVillageois;
import com.avec.model.AgentTerrain;
import com.avec.model.Membre;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des AVEC
 */
public class AvecService {

    private final AvecDAO avecDAO;
    private final AgentVillageoisDao agentVillageoisDao;
    private final AgentTerrainDao agentTerrainDAO;
    private final MembreDAO membreDAO;

    public AvecService() {
        this.avecDAO = new AvecDAO();
        this.agentVillageoisDao = new AgentVillageoisDao();
        this.agentTerrainDAO = new AgentTerrainDao();
        this.membreDAO = new MembreDAO();
    }

    /**
     * Crée une nouvelle AVEC
     */
    public Avec creerAvec(String nom, BigDecimal prixPart, String lieuReunion,
                          JourReunion jourReunion, Long agentVillageoisId)
            throws SQLException, IllegalArgumentException {

        // Vérifier que l'agent villageois existe
        AgentVillageois av = agentVillageoisDao.findAgentVillageoisById(agentVillageoisId);
        if (av == null) {
            throw new IllegalArgumentException("Agent villageois non trouvé avec l'ID: " + agentVillageoisId);
        }

        // Créer l'AVEC
        Avec avec = new Avec( nom, prixPart, lieuReunion, jourReunion);
        avec.setAgentVillageoisId(agentVillageoisId);
        avec.setStatut(StatutAvec.EN_FORMATION);
        avec.setPhaseCourante(PhaseCycle.PREPERATIORE);

        // Générer un code unique
        String codeUnique = genererCodeUnique();
        avec.setCodeUnique(codeUnique);

        return avecDAO.insert(avec);
    }

    /**
     * Modifie une AVEC existante
     */
    public boolean modifierAvec(Avec avec) throws SQLException {
        if (avec.getId() == null) {
            throw new IllegalArgumentException("L'ID de l'AVEC ne peut pas être null");
        }
        return avecDAO.update(avec);
    }

    /**
     * Supprime une AVEC
     */
    public boolean supprimerAvec(long id) throws SQLException {
        // Vérifier que l'AVEC n'a pas de membres actifs
        List<Membre> membres = membreDAO.findByAvecId(id);
        long actifs = membres.stream()
                .filter(m -> m.getStatut() == StatutMembre.ACTIF)
                .count();

        if (actifs > 0) {
            throw new IllegalStateException("Impossible de supprimer une AVEC avec des membres actifs");
        }

        return avecDAO.delete(id);
    }

    /**
     * Récupère une AVEC par son ID
     */
    public Avec getAvecById(long id) throws SQLException {
        return avecDAO.findById(id);
    }

    /**
     * Récupère toutes les AVEC
     */
    public List<Avec> getAllAvecs() throws SQLException {
        return avecDAO.findAll();
    }

    /**
     * Récupère les AVEC par statut
     */
    public List<Avec> getAvecsByStatut(StatutAvec statut) throws SQLException {
        return avecDAO.findByStatut(statut);
    }

    /**
     * Récupère les AVEC par phase
     */
    public List<Avec> getAvecsByPhase(PhaseCycle phase) throws SQLException {
        return avecDAO.findByPhase(phase);
    }

    /**
     * Récupère les AVEC d'un agent villageois
     */
    public List<Avec> getAvecsByAgentVillageois(long agentId) throws SQLException {
        return avecDAO.findByAgentVillageoisId(agentId);
    }

    /**
     * Recherche des AVEC par nom
     */
    public List<Avec> rechercherAvecs(String recherche) throws SQLException {
        if (recherche == null || recherche.trim().isEmpty()) {
            return getAllAvecs();
        }
        return avecDAO.searchByNom(recherche);
    }

    /**
     * Change la phase d'une AVEC
     */
    public boolean changerPhase(long avecId, PhaseCycle nouvellePhase) throws SQLException {
        Avec avec = avecDAO.findById(avecId);
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée");
        }

        // Vérifier que le passage est possible
        if (!peutChangerPhase(avec, nouvellePhase)) {
            throw new IllegalStateException("Impossible de passer à la phase " + nouvellePhase.getLibelle());
        }

        avec.changerPhase(nouvellePhase);
        return avecDAO.updatePhase(avecId, nouvellePhase);
    }

    /**
     * Vérifie si le changement de phase est possible
     */
    private boolean peutChangerPhase(Avec avec, PhaseCycle nouvellePhase) {
        PhaseCycle phaseActuelle = avec.getPhaseCourante();

        // Vérifier la progression logique des phases
        if (phaseActuelle == PhaseCycle.PREPERATIORE && nouvellePhase != PhaseCycle.INTENSIVE) {
            return false;
        }
        if (phaseActuelle == PhaseCycle.INTENSIVE && nouvellePhase != PhaseCycle.DEVELOPPEMENT) {
            return false;
        }
        if (phaseActuelle == PhaseCycle.DEVELOPPEMENT && nouvellePhase != PhaseCycle.MATURITE) {
            return false;
        }
        if (phaseActuelle == PhaseCycle.MATURITE && nouvellePhase != PhaseCycle.TERMINE) {
            return false;
        }

        return true;
    }

    /**
     * Valide le passage de phase par l'agent terrain
     */
    public boolean validerPassagePhase(long avecId, long agentTerrainId, PhaseCycle nouvellePhase)
            throws SQLException {

        // Vérifier que l'agent terrain existe
        AgentTerrain at = agentTerrainDAO.findAgentTerrainById(agentTerrainId);
        if (at == null) {
            throw new IllegalArgumentException("Agent terrain non trouvé");
        }

        // Vérifier que l'AVEC est supervisée par cet agent
        Avec avec = avecDAO.findById(avecId);
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée");
        }

        if (avec.getAgentTerrainId() == null || avec.getAgentTerrainId() != agentTerrainId) {
            throw new IllegalStateException("Cette AVEC n'est pas supervisée par cet agent terrain");
        }

        return changerPhase(avecId, nouvellePhase);
    }

    /**
     * Récupère les statistiques des AVEC
     */
    public StatistiquesAvec getStatistiques() throws SQLException {
        List<Avec> toutesAvecs = getAllAvecs();

        StatistiquesAvec stats = new StatistiquesAvec();
        stats.setTotalAvecs(toutesAvecs.size());

        for (Avec avec : toutesAvecs) {
            switch (avec.getStatut()) {
                case EN_FORMATION -> stats.incrementerEnFormation();
                case ACTVIE -> stats.incrementerActives();
                case EN_PAUSE -> stats.incrementerEnPause();
                case TERMINE -> stats.incrementerTerminees();
            }

            switch (avec.getPhaseCourante()) {
                case PREPERATIORE -> stats.incrementerPreparatoire();
                case INTENSIVE -> stats.incrementerIntensive();
                case DEVELOPPEMENT -> stats.incrementerDeveloppement();
                case MATURITE -> stats.incrementerMaturite();
            }
        }

        return stats;
    }

    /**
     * Génère un code unique pour une AVEC
     */
    private String genererCodeUnique() {
        String prefix = "AVEC";
        String annee = String.valueOf(LocalDate.now().getYear());
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + "-" + annee + "-" + uuid;
    }

    /**
     * Classe interne pour les statistiques des AVEC
     */
    public static class StatistiquesAvec {
        private int totalAvecs;
        private int enFormation;
        private int actives;
        private int enPause;
        private int terminees;
        private int preparatoire;
        private int intensive;
        private int developpement;
        private int maturite;

        public void incrementerEnFormation() { enFormation++; }
        public void incrementerActives() { actives++; }
        public void incrementerEnPause() { enPause++; }
        public void incrementerTerminees() { terminees++; }
        public void incrementerPreparatoire() { preparatoire++; }
        public void incrementerIntensive() { intensive++; }
        public void incrementerDeveloppement() { developpement++; }
        public void incrementerMaturite() { maturite++; }

        // Getters
        public int getTotalAvecs() { return totalAvecs; }
        public void setTotalAvecs(int totalAvecs) { this.totalAvecs = totalAvecs; }

        public int getEnFormation() { return enFormation; }
        public int getActives() { return actives; }
        public int getEnPause() { return enPause; }
        public int getTerminees() { return terminees; }

        public int getPreparatoire() { return preparatoire; }
        public int getIntensive() { return intensive; }
        public int getDeveloppement() { return developpement; }
        public int getMaturite() { return maturite; }
    }
}