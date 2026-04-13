package com.avec.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.avec.dao.AvecDAO;
import com.avec.dao.MembreDAO;
import com.avec.dao.UtilisateurDao;
import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import com.avec.model.Avec;
import com.avec.model.Membre;
import com.avec.model.Utilisateur;

public class MembreService {

    private final MembreDAO membreDAO;
    private final AvecDAO avecDAO;
    private final UtilisateurDao utilisateurDao;

    public MembreService() {
        this.membreDAO = new MembreDAO();
        this.avecDAO = new AvecDAO();
        this.utilisateurDao = new UtilisateurDao();
    }

    /**
     * Crée un nouveau membre et son compte utilisateur
     */
    public Membre creerMembre(String nom, String prenom, Long avecId,
                              String motDePasse, String telephone)
            throws SQLException, IllegalArgumentException {

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (avecId == null) {
            throw new IllegalArgumentException("L'ID de l'AVEC est obligatoire");
        }
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire");
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

        // 1. Créer l'utilisateur dans la table utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(numeroCarte + "@membre.avec.com"); // Email par défaut
        utilisateur.setMotDePasse(motDePasse);
        utilisateur.setTelephone(telephone);
        
        utilisateurDao.ajouter(utilisateur);

        // 2. Créer le membre dans la table membre
        Membre membre = new Membre();
        membre.setId(utilisateur.getId()); // Même ID que l'utilisateur
        membre.setNom(nom);
        membre.setPrenom(prenom);
        membre.setNumeroCarte(numeroCarte);
        membre.setEstActif(StatutMembre.ACTIF);
        membre.setDateAdhesion(LocalDate.now());
        membre.setAvecId(avecId);
        membre.setRoleComite(RoleComite.AUCUN);
        membre.setRoleCle(RoleDetenteurCle.AUCUN);

        return membreDAO.insert(membre);
    }
    
    /**
     * Crée un nouveau membre et son compte utilisateur
     */
    public Membre creerMembreSimple(String nom, String prenom, Long avecId, String telephone)
            throws SQLException, IllegalArgumentException {

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        if (avecId == null) {
            throw new IllegalArgumentException("L'ID de l'AVEC est obligatoire");
        }
       
        
        
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire");
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

       

        // 2. Créer le membre dans la table membre
        Membre membre = new Membre();
        membre.setNom(nom);
        membre.setPrenom(prenom);
        membre.setNumeroCarte(numeroCarte);
        membre.setEstActif(StatutMembre.ACTIF);
        membre.setDateAdhesion(LocalDate.now());
        membre.setAvecId(avecId);
        membre.setRoleComite(RoleComite.AUCUN);
        membre.setRoleCle(RoleDetenteurCle.AUCUN);

        return membreDAO.insert(membre);
    }
    
    /**
     * Cherche un membre par carte et mot de passe
     */
    public Membre chercherParCarteEtMotDePasse(String numeroCarte, String motDePasse) {
        if (numeroCarte == null || numeroCarte.trim().isEmpty()) {
            return null;
        }
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            return null;
        }
        return membreDAO.chercherParCarteEtMotDePasse(numeroCarte, motDePasse);
    }
    
    /**
     * Met à jour le mot de passe d'un membre
     */
    public boolean updateMotDePasse(long membreId, String nouveauMotDePasse) throws SQLException {
        Utilisateur utilisateur = utilisateurDao.chercherId(membreId);
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        utilisateur.setMotDePasse(nouveauMotDePasse);
        return utilisateurDao.modifier(utilisateur);
    }
    
    /**
     * Modifie un membre existant
     */
    public boolean modifierMembre(Membre membre) throws SQLException {
        if (membre.getId() == null) {
            throw new IllegalArgumentException("L'ID du membre ne peut pas être nul");
        }
        
        // Mettre à jour le membre
        boolean updated = membreDAO.update(membre);
        
        // Mettre à jour l'utilisateur associé
        if (updated) {
            Utilisateur utilisateur = utilisateurDao.chercherId(membre.getId());
            if (utilisateur != null) {
                utilisateur.setNom(membre.getNom());
                utilisateur.setPrenom(membre.getPrenom());
                utilisateurDao.modifier(utilisateur);
            }
        }
        
        return updated;
    }

    /**
     * Désactive un membre
     */
    public boolean desactiverMembre(long membreId) throws SQLException {
        Membre membre = membreDAO.findById(membreId);
        if (membre == null) {
            throw new IllegalArgumentException("Membre non trouvé");
        }

        membre.setEstActif(StatutMembre.INACTIF);
        return membreDAO.update(membre);
    }

    /**
     * Organise l'élection du comité
     */
    public boolean organiserElection(long avecId, List<ResultatElection> resultats) throws SQLException {
        if (resultats.size() != 5) {
            throw new IllegalArgumentException("Le comité doit avoir 5 membres");
        }

        membreDAO.resetAllRolesComite(avecId);

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
     * ✅ Récupère TOUS les membres de toutes les AVEC
     */
    public List<Membre> getAllMembres() throws SQLException {
        return membreDAO.findAll();
    }

    /**
     * ✅ Récupère le nombre total de membres
     */
    public int getNombreTotalMembres() throws SQLException {
        return membreDAO.countAll();
    }

    /**
     * ✅ Récupère le nombre de membres actifs
     */
    public int getNombreMembresActifs() throws SQLException {
        return membreDAO.countActifs();
    }

    /**
     * Récupère tous les membres d'une AVEC
     */
    public List<Membre> getMembresByAvecId(long avecId) throws SQLException {
        return membreDAO.findByAvecId(avecId);
    }
    
    /**
     * Récupère tous les membres d'une AVEC
     */
    public List<Membre> getMembreByAvecId(long avecId) throws SQLException {
        return membreDAO.findsByAvecId(avecId);
    }
    /**
     * ✅ Désigne les gardiens de clés pour une AVEC
     * Les gardiens de clés sont 3 membres qui ne font pas partie du comité
     */
    public boolean designerGardiensCles(long avecId, List<Long> idsGardiens) throws SQLException {
        // Vérifier qu'on a exactement 3 gardiens
        if (idsGardiens.size() != 3) {
            throw new IllegalArgumentException("Il faut exactement 3 gardiens de clés");
        }

        // Réinitialiser tous les rôles de clé pour cette AVEC
        membreDAO.resetAllRolesCle(avecId);

        // Récupérer le comité pour vérification
        List<Membre> comite = membreDAO.findComiteGestion(avecId);
        List<Long> idsComite = comite.stream()
                .map(Membre::getId)
                .toList();

        // Assigner les rôles de gardiens
        for (int i = 0; i < idsGardiens.size(); i++) {
            long membreId = idsGardiens.get(i);
            Membre membre = membreDAO.findById(membreId);

            if (membre == null) {
                throw new IllegalArgumentException("Membre non trouvé avec l'ID: " + membreId);
            }

            if (membre.getAvecId() != avecId) {
                throw new IllegalArgumentException("Le membre n'appartient pas à cette AVEC");
            }

            // Vérifier que le membre n'est pas au comité
            if (idsComite.contains(membreId)) {
                throw new IllegalArgumentException("Les gardiens de clés ne doivent pas être au comité");
            }

            // Vérifier que le membre est actif
            if (membre.getEstActif() != StatutMembre.ACTIF) {
                throw new IllegalArgumentException("Le membre doit être actif");
            }

            // Déterminer le rôle du gardien
            RoleDetenteurCle role = switch(i) {
                case 0 -> RoleDetenteurCle.GARDIEN_CLE_1;
                case 1 -> RoleDetenteurCle.GARDIEN_CLE_2;
                case 2 -> RoleDetenteurCle.GARDIEN_CLE_3;
                default -> RoleDetenteurCle.AUCUN;
            };

            membre.setRoleCle(role);
            membreDAO.updateRoleCle(membreId, role.name());
        }

        return true;
    }


    /**
     * Récupère un membre par son ID
     */
    public Membre getMembreById(long id) throws SQLException {
        return membreDAO.findById(id);
    }

    /**
     * Récupère le comité de gestion
     */
    public List<Membre> getComiteGestion(long avecId) throws SQLException {
        return membreDAO.findComiteGestion(avecId);
    }

    /**
     * Génère un numéro de carte unique
     */
    private String genererNumeroCarte(long avecId) {
        String prefix = "MEM";
        String avec = String.format("%03d", avecId);
        String unique = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + "-" + avec + "-" + unique;
    }

    public static class ResultatElection {
        private final long membreId;
        private final RoleComite role;

        public ResultatElection(long membreId, RoleComite role) {
            this.membreId = membreId;
            this.role = role;
        }

        public long getMembreId() { return membreId; }
        public RoleComite getRole() { return role; }
    }
}