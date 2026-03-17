package com.avec.service;

import com.avec.dao.MembreDAO;
import com.avec.dao.AvecDAO;
import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import com.avec.model.Membre;
import com.avec.model.Avec;
import com.avec.utils.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des membres
 */
public class MembreService {

    private final MembreDAO membreDAO;
    private final AvecDAO avecDAO;

    public MembreService() {
        this.membreDAO = new MembreDAO();
        this.avecDAO = new AvecDAO();
    }

    /**
     * Crée un nouveau membre
     */
    public Membre creerMembre(String nom, String prenom, Long avecId,
                              String profession, String village, String telephone)
            throws SQLException, IllegalArgumentException {

        // Validation
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (avecId == null) {
            throw new IllegalArgumentException("L'ID de l'AVEC est obligatoire");
        }

        // Vérifier que l'AVEC existe
        Avec avec = avecDAO.findById(avecId);
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée avec l'ID: " + avecId);
        }

        // Vérifier le nombre maximum de membres
        int nombreMembresActifs = membreDAO.countActifsByAvecId(avecId);
        if (nombreMembresActifs >= avec.getNombreMembresMax()) {
            throw new IllegalArgumentException("Nombre maximum de membres atteint (" +
                    avec.getNombreMembresMax() + ")");
        }

        // Générer un numéro de carte unique
        String numeroCarte = genererNumeroCarte(avecId);

        Membre membre = new Membre(nom, prenom, numeroCarte, LocalDate.now());
        membre.setAvecId(avecId);
        membre.setProfession(profession);
        membre.setVillage(village);
        membre.setTelephone(telephone);
        membre.setStatut(StatutMembre.ACTIF);
        membre.setRoleComite(RoleComite.AUCUN);
        membre.setRoleCle(RoleDetenteurCle.AUCUN);

        return membreDAO.insert(membre);
    }

    /**
     * Modifie un membre existant
     */
    public boolean modifierMembre(Membre membre) throws SQLException {
        if (membre.getId() == null) {
            throw new IllegalArgumentException("L'ID du membre ne peut pas être nul");
        }

        Membre existant = membreDAO.findById(membre.getId());
        if (existant == null) {
            throw new IllegalArgumentException("Membre non trouvé");
        }

        return membreDAO.update(membre);
    }

    /**
     * Désactive un membre (départ de l'AVEC)
     */
    public boolean desactiverMembre(long membreId) throws SQLException {
        Membre membre = membreDAO.findById(membreId);
        if (membre == null) {
            throw new IllegalArgumentException("Membre non trouvé");
        }

        // Vérifier qu'il n'a pas de prêts en cours
        if (membre.getTotalPretEnCours().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Impossible de désactiver : le membre a des prêts en cours");
        }

        membre.setStatut(StatutMembre.INACTIF);
        return membreDAO.update(membre);
    }

    /**
     * Organise l'élection du comité de gestion
     */
    public boolean organiserElection(long avecId, List<ResultatElection> resultats) throws SQLException {
        // Vérifier qu'on a 5 résultats (Président, Secrétaire, Trésorier, 2 Compteurs)
        if (resultats.size() != 5) {
            throw new IllegalArgumentException("Le comité doit avoir 5 membres");
        }

        // Réinitialiser tous les rôles
        membreDAO.resetAllRolesComite(avecId);

        // Assigner les nouveaux rôles
        for (ResultatElection resultat : resultats) {
            Membre membre = membreDAO.findById(resultat.getMembreId());
            if (membre != null && membre.getAvecId() == avecId) {
                if (!membre.isEligibleComite()) {
                    throw new IllegalStateException("Le membre " + membre.getNomComplet() + " n'est pas éligible");
                }
                membre.setRoleComite(resultat.getRole());
                membreDAO.updateRoleComite(membre.getId(), resultat.getRole());
            }
        }

        return true;
    }

    /**
     * Désigne les gardiens de clés
     */
    public boolean designerGardiensCles(long avecId, List<Long> idsGardiens) throws SQLException {
        // Vérifier qu'on a exactement 3 gardiens
        if (idsGardiens.size() != 3) {
            throw new IllegalArgumentException("Il faut exactement 3 gardiens de clés");
        }

        // Réinitialiser tous les rôles de clé
        membreDAO.resetAllRolesCle(avecId);

        // Récupérer le comité pour vérification
        List<Membre> comite = membreDAO.findComiteGestion(avecId);

        // Assigner les rôles
        for (int i = 0; i < idsGardiens.size(); i++) {
            long membreId = idsGardiens.get(i);
            Membre membre = membreDAO.findById(membreId);

            if (membre == null || membre.getAvecId() != avecId) {
                throw new IllegalArgumentException("Membre invalide: " + membreId);
            }

            // Vérifier que le membre n'est pas au comité
            if (membre.getRoleComite() != RoleComite.AUCUN) {
                throw new IllegalArgumentException("Les gardiens de clés ne doivent pas être au comité");
            }

            // Vérifier que le membre est actif
            if (membre.getStatut() != StatutMembre.ACTIF) {
                throw new IllegalArgumentException("Le membre doit être actif");
            }

            RoleDetenteurCle role = switch(i) {
                case 0 -> RoleDetenteurCle.GARDIEN_CLE_1;
                case 1 -> RoleDetenteurCle.GARDIEN_CLE_2;
                case 2 -> RoleDetenteurCle.GARDIEN_CLE_3;
                default -> RoleDetenteurCle.AUCUN;
            };

            membreDAO.updateRoleCle(membreId, role);
        }

        return true;
    }

    /**
     * Récupère tous les membres d'une AVEC
     */
    public List<Membre> getMembresByAvecId(long avecId) throws SQLException {
        return membreDAO.findByAvecId(avecId);
    }

    /**
     * Récupère un membre par son ID
     */
    public Membre getMembreById(long id) throws SQLException {
        return membreDAO.findById(id);
    }

    /**
     * Récupère le comité de gestion d'une AVEC
     */
    public List<Membre> getComiteGestion(long avecId) throws SQLException {
        return membreDAO.findComiteGestion(avecId);
    }

    /**
     * Récupère les gardiens de clés d'une AVEC
     */
    public List<Membre> getGardiensCles(long avecId) throws SQLException {
        return membreDAO.findGardiensCles(avecId);
    }

    /**
     * Vérifie si un membre peut être gardien de clé
     */
    public boolean isEligibleGardienCle(long membreId) throws SQLException {
        Membre membre = membreDAO.findById(membreId);
        return membre != null && membre.isEligibleGardienCle();
    }

    /**
     * Met à jour l'épargne d'un membre
     */
    public boolean mettreAJourEpargne(long membreId, int nombrePartsAchetees) throws SQLException {
        Membre membre = membreDAO.findById(membreId);
        if (membre == null) {
            return false;
        }

        int nouveauNombreParts = membre.getNombreParts() + nombrePartsAchetees;
        membre.setNombreParts(nouveauNombreParts);
        membre.calculerTotalEpargne();

        return membreDAO.updateTotaux(
                membreId,
                membre.getTotalEpargne(),
                membre.getTotalPretEnCours(),
                nouveauNombreParts
        );
    }

    /**
     * Recherche des membres par nom
     */
    public List<Membre> rechercherMembres(long avecId, String recherche) throws SQLException {
        if (recherche == null || recherche.trim().isEmpty()) {
            return getMembresByAvecId(avecId);
        }
        return membreDAO.searchByNom(avecId, recherche);
    }

    /**
     * Génère un numéro de carte unique pour un membre
     */
    private String genererNumeroCarte(long avecId) {
        String prefix = "MEM";
        String avec = String.format("%03d", avecId);
        String unique = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + "-" + avec + "-" + unique;
    }

    /**
     * Classe interne pour représenter un résultat d'élection
     */
    public static class ResultatElection {
        private final long membreId;
        private final RoleComite role;

        public ResultatElection(long membreId, RoleComite role) {
            this.membreId = membreId;
            this.role = role;
        }

        public long getMembreId() {
            return membreId;
        }

        public RoleComite getRole() {
            return role;
        }
    }
}