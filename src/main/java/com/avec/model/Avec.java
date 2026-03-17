package com.avec.model;

import com.avec.config.DBConnection;
import com.avec.dao.CaisseDAO;
import com.avec.enums.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une Association Villageoise d'Épargne et de Crédit (AVEC)
 */
public class Avec {

    private Long id;
    private String nom;
    private String prenom;
    private String codeUnique;
    private StatutAvec statut;
    private LocalDate dateCreation;
    private int nombreMembresMax;
    private BigDecimal prixPart;
    private BigDecimal tauxFraisServiceMensuel;
    private PhaseCycle phaseCourante;
    private LocalDate dateDebutCycle;
    private LocalDate dateFinCyclePrevue;
    private String lieuReunion;
    private JourReunion jourReunion;
    private LocalTime heureReunion;
    private LocalDate prochaineReunion;
    private BigDecimal cotisationCaisseSolidarite;
    private boolean caisseSolidariteActive;

    // Statistiques calculées
    private int nombreMembresActifs;
    private BigDecimal totalEpargne;
    private BigDecimal totalCredit;
    private int progressionFormation;

    // Relations
    private Long agentVillageoisId;
    private AgentVillageois agentVillageois;

    private Long agentTerrainId;
    private AgentTerrain agentTerrain;

    private Caisse caisse;

    // Listes
    private List<Membre> membres;  // CORRECTION: Utilisation de Membre (pas Member)
    private List<Cycle> cycles;
    private List<Regle> reglesPersonnalisees;
    private List<Visite> visites;

    /**
     * Constructeur par défaut
     */
    public Avec() {
        this.dateCreation = LocalDate.now();
        this.statut = StatutAvec.EN_FORMATION;
        this.phaseCourante = PhaseCycle.PREPERATIORE;  // CORRECTION orthographe
        this.nombreMembresMax = 25;
        this.tauxFraisServiceMensuel = BigDecimal.valueOf(10);
        this.cotisationCaisseSolidarite = BigDecimal.valueOf(100);
        this.caisseSolidariteActive = true;
        this.heureReunion = LocalTime.of(9, 0);
        this.membres = new ArrayList<>();  // CORRECTION
        this.cycles = new ArrayList<>();
        this.reglesPersonnalisees = new ArrayList<>();
        this.visites = new ArrayList<>();
        this.totalEpargne = BigDecimal.ZERO;
        this.totalCredit = BigDecimal.ZERO;  // CORRECTION
    }

    /**
     * Constructeur avec paramètres principaux
     */
    public Avec(String nom, String prenom, BigDecimal prixPart, String lieuReunion, JourReunion jourReunion) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.prixPart = prixPart;
        this.lieuReunion = lieuReunion;
        this.jourReunion = jourReunion;
        genererCodeUnique();
    }

    public Avec(String nom, BigDecimal prixPart, String lieuReunion, JourReunion jourReunion) {
    }

    /**
     * Génère un code unique pour l'AVEC
     */
    private void genererCodeUnique() {
        String prefix = "AVEC";
        String date = String.valueOf(LocalDate.now().getYear());
        int random = (int) (Math.random() * 1000);
        this.codeUnique = String.format("%s-%s-%03d", prefix, date, random);
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getCodeUnique() {
        return codeUnique;
    }

    public void setCodeUnique(String codeUnique) {
        this.codeUnique = codeUnique;
    }

    public StatutAvec getStatut() {
        return statut;
    }

    public void setStatut(StatutAvec statut) {
        this.statut = statut;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getNombreMembresMax() {
        return nombreMembresMax;
    }

    public void setNombreMembresMax(int nombreMembresMax) {
        if (nombreMembresMax < 15 || nombreMembresMax > 30) {
            throw new IllegalArgumentException("Le nombre de membres doit être entre 15 et 30");
        }
        this.nombreMembresMax = nombreMembresMax;
    }

    public BigDecimal getPrixPart() {
        return prixPart;
    }

    public void setPrixPart(BigDecimal prixPart) {
        if (prixPart == null || prixPart.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix de la part doit être positif");
        }
        this.prixPart = prixPart;
    }

    public BigDecimal getTauxFraisServiceMensuel() {
        return tauxFraisServiceMensuel;
    }

    public void setTauxFraisServiceMensuel(BigDecimal taux) {
        if (taux == null) {
            throw new IllegalArgumentException("Le taux ne peut pas être null");
        }
        if (taux.compareTo(BigDecimal.valueOf(5)) < 0 || taux.compareTo(BigDecimal.valueOf(10)) > 0) {
            throw new IllegalArgumentException("Le taux doit être entre 5% et 10%");
        }
        this.tauxFraisServiceMensuel = taux;
    }

    public PhaseCycle getPhaseCourante() {
        return phaseCourante;
    }

    public void setPhaseCourante(PhaseCycle phaseCourante) {
        this.phaseCourante = phaseCourante;
    }

    public LocalDate getDateDebutCycle() {
        return dateDebutCycle;
    }

    public void setDateDebutCycle(LocalDate dateDebutCycle) {
        this.dateDebutCycle = dateDebutCycle;  // Suppression de la validation non-null
    }

    public LocalDate getDateFinCyclePrevue() {
        return dateFinCyclePrevue;
    }

    public void setDateFinCyclePrevue(LocalDate dateFinCyclePrevue) {
        this.dateFinCyclePrevue = dateFinCyclePrevue;  // Suppression de la validation non-null
    }

    public String getLieuReunion() {
        return lieuReunion;
    }

    public void setLieuReunion(String lieuReunion) {
        this.lieuReunion = lieuReunion;  // Suppression de la validation non-null
    }

    public JourReunion getJourReunion() {
        return jourReunion;
    }

    public void setJourReunion(JourReunion jourReunion) {
        this.jourReunion = jourReunion;  // Suppression de la validation non-null
    }

    public LocalTime getHeureReunion() {
        return heureReunion;
    }

    public void setHeureReunion(LocalTime heureReunion) {
        this.heureReunion = heureReunion;  // Suppression de la validation non-null
    }

    public LocalDate getProchaineReunion() {
        return prochaineReunion;
    }

    public void setProchaineReunion(LocalDate prochaineReunion) {
        this.prochaineReunion = prochaineReunion;  // Suppression de la validation non-null
    }

    public BigDecimal getCotisationCaisseSolidarite() {
        return cotisationCaisseSolidarite;
    }

    public void setCotisationCaisseSolidarite(BigDecimal cotisationCaisseSolidarite) {
        this.cotisationCaisseSolidarite = cotisationCaisseSolidarite;
    }

    public boolean isCaisseSolidariteActive() {
        return caisseSolidariteActive;
    }

    public void setCaisseSolidariteActive(boolean caisseSolidariteActive) {
        this.caisseSolidariteActive = caisseSolidariteActive;
    }

    public int getNombreMembresActifs() {
        return nombreMembresActifs;
    }

    public void setNombreMembresActifs(int nombreMembresActifs) {
        this.nombreMembresActifs = nombreMembresActifs;
    }

    public BigDecimal getTotalEpargne() {
        return totalEpargne;
    }

    public void setTotalEpargne(BigDecimal totalEpargne) {
        this.totalEpargne = totalEpargne;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public int getProgressionFormation() {
        return progressionFormation;
    }

    public void setProgressionFormation(int progressionFormation) {
        this.progressionFormation = progressionFormation;
    }

    public Long getAgentVillageoisId() {
        return agentVillageoisId;
    }

    public void setAgentVillageoisId(Long agentVillageoisId) {
        this.agentVillageoisId = agentVillageoisId;
    }

    public AgentVillageois getAgentVillageois() {
        return agentVillageois;  // CORRECTION
    }

    public void setAgentVillageois(AgentVillageois agentVillageois) {
        this.agentVillageois = agentVillageois;
        if (agentVillageois != null) {
            this.agentVillageoisId = agentVillageois.getId();
        }
    }

    public Long getAgentTerrainId() {
        return agentTerrainId;
    }

    public void setAgentTerrainId(Long agentTerrainId) {
        this.agentTerrainId = agentTerrainId;
    }

    public AgentTerrain getAgentTerrain() {
        return agentTerrain;
    }

    public void setAgentTerrain(AgentTerrain agentTerrain) {
        this.agentTerrain = agentTerrain;
        if (agentTerrain != null) {
            this.agentTerrainId = agentTerrain.getId();
        }
    }

    public Caisse getCaisse() {
        return caisse;
    }

    public void setCaisse(Caisse caisse) {
        this.caisse = caisse;
    }

    public List<Membre> getMembres() {  // CORRECTION: nom de méthode
        return membres;
    }

    public void setMembres(List<Membre> membres) {  // CORRECTION
        this.membres = membres;
        calculerStatistiques();
    }

    public List<Cycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<Cycle> cycles) {
        this.cycles = cycles;
    }

    public List<Regle> getReglesPersonnalisees() {
        return reglesPersonnalisees;
    }

    public void setReglesPersonnalisees(List<Regle> reglesPersonnalisees) {
        this.reglesPersonnalisees = reglesPersonnalisees;
    }

    public List<Visite> getVisites() {
        return visites;
    }

    public void setVisites(List<Visite> visites) {
        this.visites = visites;
        calculerProgressionFormation();
    }

    // Méthodes métier

    /**
     * Calcule les statistiques de l'AVEC
     */
    public void calculerStatistiques() {
        if (membres == null) return;

        // Membres actifs
        long actifs = membres.stream()
                .filter(m -> m != null && m.getStatut() == StatutMembre.ACTIF)
                .count();
        this.nombreMembresActifs = (int) actifs;

        // Total Épargne
        this.totalEpargne = membres.stream()
                .filter(m -> m != null)
                .map(Membre::getTotalEpargne)
                .filter(ep -> ep != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total crédits en cours
        this.totalCredit = membres.stream()
                .filter(m -> m != null && m.getPrets() != null)
                .flatMap(m -> m.getPrets().stream())
                .filter(p -> p != null && p.getStatut() == StatutPret.ACTIF)
                .map(Pret::getMontantTotalDu)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule la progression de la formation
     */
    public void calculerProgressionFormation() {
        if (visites == null || visites.isEmpty()) {
            this.progressionFormation = 0;
            return;
        }
        long visitesCompletees = visites.stream()
                .filter(Visite::isModuleComplete)
                .count();

        this.progressionFormation = (int) ((visitesCompletees * 100) / 15);
        if (this.progressionFormation > 100) {
            this.progressionFormation = 100;
        }
    }

    /**
     * Démarre un nouveau cycle
     */
    public Cycle demarrerNouveauCycle() {
        if (!peutDemarrerNouveauCycle()) {  // CORRECTION: appel de méthode avec ()
            throw new IllegalStateException("Impossible de démarrer un nouveau cycle: un cycle est déjà en cours");
        }
        Cycle cycle = new Cycle();
        cycle.setAvec(this);
        cycle.setAvecId(this.id);
        cycle.setDateDebut(LocalDate.now());
        cycle.setDateFinPrevue(LocalDate.now().plusWeeks(36));
        cycle.setNumeroCycle(cycles.size() + 1);
        cycle.setStatut(StatutCycle.EN_COURS);

        cycles.add(cycle);
        this.dateDebutCycle = cycle.getDateDebut();
        this.dateFinCyclePrevue = cycle.getDateFinPrevue();

        return cycle;
    }

    /**
     * Vérifie si un nouveau cycle peut démarrer
     */
    public boolean peutDemarrerNouveauCycle() {
        if (cycles == null || cycles.isEmpty()) {
            return true;
        }
        return cycles.stream().allMatch(c -> c.getStatut() == StatutCycle.TERMINE);
    }

    /**
     * Change la phase courante
     */
    public void changerPhase(PhaseCycle nouvellePhase) {
        if (nouvellePhase == null) {
            throw new IllegalArgumentException("La phase ne peut pas être null");
        }
        this.phaseCourante = nouvellePhase;

        // Mise à jour du statut si nécessaire
        if (nouvellePhase == PhaseCycle.TERMINE) {
            this.statut = StatutAvec.TERMINE;
        }
    }

    /**
     * Vérifie si l'AVEC peut passer à la phase suivante
     */
    public boolean peutPasserPhaseSuivante() {
        if (visites == null) return false;

        return switch (phaseCourante) {
            case PREPERATIORE -> visites.size() >= 1;  // CORRECTION orthographe
            case INTENSIVE -> visites.size() >= 10;
            case DEVELOPPEMENT -> visites.size() >= 13;
            case MATURITE -> visites.size() >= 15;
            default -> false;
        };
    }

    /**
     * Ajoute un membre à l'AVEC
     */
    public void ajouterMembre(Membre membre) {
        if (membres == null) {
            membres = new ArrayList<>();
        }

        if (membres.size() >= nombreMembresMax) {
            throw new IllegalStateException("Nombre maximum de membres atteint: " + nombreMembresMax);
        }

        membres.add(membre);
        membre.setAvec(this);
        membre.setAvecId(this.id);
        calculerStatistiques();
    }

    /**
     * Retire un membre de l'AVEC
     */
    public boolean retirerMembre(Membre membre) {
        if (membres == null) return false;

        boolean removed = membres.remove(membre);
        if (removed) {
            calculerStatistiques();
        }
        return removed;
    }

    /**
     * Récupère le cycle en cours
     */
    public Cycle getCycleEnCours() {
        if (cycles == null) return null;

        return cycles.stream()
                .filter(c -> c.getStatut() == StatutCycle.EN_COURS)
                .findFirst()
                .orElse(null);
    }

    /**
     * Récupère le comité de gestion
     */
    public List<Membre> getComiteGestion() {
        if (membres == null) return new ArrayList<>();

        return membres.stream()
                .filter(m -> m.getRoleComite() != null &&
                        m.getRoleComite() != RoleComite.AUCUN)
                .toList();
    }

    /**
     * Récupère les gardiens de clés
     */
    public List<Membre> getGardiensCles() {
        if (membres == null) return new ArrayList<>();

        return membres.stream()
                .filter(m -> m.getRoleCle() != null &&
                        m.getRoleCle() != RoleDetenteurCle.AUCUN)
                .toList();
    }

    /**
     * Vérifie si tous les postes du comité sont pourvus
     */
    public boolean isComiteComplet() {
        return getComiteGestion().size() == 5;
    }

    /**
     * Vérifie si les 3 gardiens de clés sont désignés
     */
    public boolean isGardiensClesComplets() {
        return getGardiensCles().size() == 3;
    }

    /**
     * Vérifie si l'AVEC est prête à fonctionner
     */
    public boolean isPretPourFonctionnement() {
        return isComiteComplet() &&
                isGardiensClesComplets() &&
                prixPart != null &&
                prixPart.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Trouve une AVEC avec sa caisse
     */
    public Avec findByIdWithCaisse(long id) throws SQLException {
        Avec avec = findAvecById(id);
        if (avec != null) {
            CaisseDAO caisseDAO = new CaisseDAO();
            Caisse caisse = caisseDAO.findByAvecId(id);
            avec.setCaisse(caisse);
        }
        return avec;
    }
// trouver un Avec per son Id
    private Avec findAvecById(long id) throws SQLException{
        String sql = "SELECT * FROM avec WHERE id = ?";
        try(Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1,id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()){
                    Avec avec = new Avec();

                    //Remplir les proprietes
                    avec.setId(rs.getLong("id"));
                    avec.setNom(rs.getNString("mom"));
                    avec.setCodeUnique(rs.getString("codeUnique"));
                    avec.setStatut(StatutAvec.valueOf(rs.getString("statut")));
                    avec.setDateCreation(rs.getDate("dateCreation").toLocalDate());
                    avec.setPrixPart(rs.getBigDecimal("prixPart"));
                    avec.setTauxFraisServiceMensuel(rs.getBigDecimal("tauxFraisServiceMensuel"));
                    avec.setPhaseCourante(PhaseCycle.valueOf(rs.getString("phaseCourante")));
                    Date dateDebutCycle = rs.getDate("dateDebutCycle");
                    if (dateDebutCycle != null){
                        avec.setDateDebutCycle(dateDebutCycle.toLocalDate());
                    }
                    Date dateFinCyclePrevue = rs.getDate("dateFinCyclePrevue");
                    if (dateFinCyclePrevue != null){
                        avec.setDateFinCyclePrevue(dateFinCyclePrevue.toLocalDate());
                    }
                    avec.setLieuReunion(rs.getString("lieuReunion"));

                    String jourReunion = rs.getString("jourReunion");
                    if (jourReunion != null){
                        avec.setJourReunion(JourReunion.valueOf(jourReunion));
                    }

                    Time heureReunion =rs.getTime("heureReunion");
                    if (heureReunion != null){
                        avec.setHeureReunion(heureReunion.toLocalTime());
                    }

                    Date prochaineReunion = rs.getDate("prochaineReunion");
                    if(prochaineReunion != null){
                        avec.setProchaineReunion(prochaineReunion.toLocalDate());
                    }

                    avec.setCotisationCaisseSolidarite(rs.getBigDecimal("cotisationCaisseSolidarite"));
                    avec.setAgentVillageoisId(rs.getLong("agentVillageoisId"));

                    long agentTerrainId = rs.getLong("agentTerrainId");
                    if ( !rs.wasNull()){
                        avec.setAgentTerrainId((agentTerrainId));
                    }
                    return avec;
                 }

            }
        }
        return null;
    }

    @Override
    public String toString() {
        return nom + " (" + codeUnique + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avec avec = (Avec) o;
        return id != null && id.equals(avec.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}