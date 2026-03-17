package com.avec.model;

import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import com.avec.enums.StatutPret;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un membre d'une AVEC
 */
public class Membre {

    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String numeroCarte;
    private StatutMembre statut;
    private LocalDate dateAdhesion;
    private String profession;
    private String village;
    private String telephone;
    private RoleComite roleComite;
    private RoleDetenteurCle roleCle;
    private BigDecimal totalEpargne;
    private BigDecimal totalPretEnCours;
    private int nombreParts;

    // Relations
    private Long avecId;
    private Avec avec;

    private List<AchatPart> achatsParts;
    private List<Pret> prets;

    /**
     * Constructeur par défaut
     */
    public Membre() {
        this.statut = StatutMembre.ACTIF;
        this.roleComite = RoleComite.AUCUN;
        this.roleCle = RoleDetenteurCle.AUCUN;
        this.totalEpargne = BigDecimal.ZERO;
        this.totalPretEnCours = BigDecimal.ZERO;
        this.nombreParts = 0;
        this.achatsParts = new ArrayList<>();
        this.prets = new ArrayList<>();
    }

    /**
     * Constructeur avec paramètres principaux
     */
    public Membre(String nom, String prenom, String numeroCarte, LocalDate dateAdhesion) {
        this();
        this.nom = nom != null ? nom.toUpperCase() : null;
        this.prenom = prenom;
        this.numeroCarte = numeroCarte;
        this.dateAdhesion = dateAdhesion;
        this.nomComplet = (prenom != null ? prenom + " " : "") + (nom != null ? nom.toUpperCase() : "");
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
        this.nom = nom != null ? nom.toUpperCase() : null;
        mettreAJourNomComplet();
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
        mettreAJourNomComplet();
    }

    public String getNomComplet() {
        return nomComplet;
    }

    private void mettreAJourNomComplet() {
        StringBuilder sb = new StringBuilder();
        if (prenom != null && !prenom.isEmpty()) {
            sb.append(prenom);
        }
        if (nom != null && !nom.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(nom);
        }
        this.nomComplet = sb.toString();
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public StatutMembre getStatut() {
        return statut;
    }

    public void setStatut(StatutMembre statut) {
        this.statut = statut;
    }

    public LocalDate getDateAdhesion() {
        return dateAdhesion;
    }

    public void setDateAdhesion(LocalDate dateAdhesion) {
        this.dateAdhesion = dateAdhesion;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public RoleComite getRoleComite() {
        return roleComite;
    }

    public void setRoleComite(RoleComite roleComite) {
        this.roleComite = roleComite;
    }

    public RoleDetenteurCle getRoleCle() {
        return roleCle;
    }

    public void setRoleCle(RoleDetenteurCle roleCle) {
        this.roleCle = roleCle;
    }

    public BigDecimal getTotalEpargne() {
        return totalEpargne;
    }

    public void setTotalEpargne(BigDecimal totalEpargne) {
        this.totalEpargne = totalEpargne != null ? totalEpargne : BigDecimal.ZERO;
    }

    public BigDecimal getTotalPretEnCours() {
        return totalPretEnCours;
    }

    public void setTotalPretEnCours(BigDecimal totalPretEnCours) {
        this.totalPretEnCours = totalPretEnCours != null ? totalPretEnCours : BigDecimal.ZERO;
    }

    public int getNombreParts() {
        return nombreParts;
    }

    public void setNombreParts(int nombreParts) {
        this.nombreParts = nombreParts;
    }

    public Long getAvecId() {
        return avecId;
    }

    public void setAvecId(Long avecId) {
        this.avecId = avecId;
    }

    public Avec getAvec() {
        return avec;
    }

    public void setAvec(Avec avec) {
        this.avec = avec;
        if (avec != null) {
            this.avecId = avec.getId();
        }
    }

    public List<AchatPart> getAchatsParts() {
        return achatsParts;
    }

    public void setAchatsParts(List<AchatPart> achatsParts) {
        this.achatsParts = achatsParts;
        calculerTotalEpargne();
    }

    public List<Pret> getPrets() {
        return prets;
    }

    public void setPrets(List<Pret> prets) {
        this.prets = prets;
        calculerTotalPretEnCours();
    }

    // Méthodes métier

    /**
     * Calcule le nombre total de parts achetées
     */
    public int calculerNombrePartsTotal() {
        if (achatsParts == null) return 0;

        int total = achatsParts.stream()
                .mapToInt(AchatPart::getNombreParts)
                .sum();
        this.nombreParts = total;
        return total;
    }

    /**
     * Calcule le total de l'épargne (nombreParts * prixPart)
     */
    public BigDecimal calculerTotalEpargne() {
        if (avec == null || avec.getPrixPart() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.valueOf(calculerNombrePartsTotal())
                .multiply(avec.getPrixPart());
        this.totalEpargne = total;
        return total;
    }

    /**
     * Calcule le total des prêts en cours
     */
    public BigDecimal calculerTotalPretEnCours() {
        if (prets == null) return BigDecimal.ZERO;

        BigDecimal total = prets.stream()
                .filter(p -> p.getStatut() == StatutPret.ACTIF)
                .map(Pret::getMontantTotalDu)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalPretEnCours = total;
        return total;
    }

    /**
     * Calcule la capacité d'emprunt (3 × épargne)
     */
    public BigDecimal calculerCapaciteEmprunt() {
        return getTotalEpargne().multiply(BigDecimal.valueOf(3));
    }

    /**
     * Vérifie si le membre peut emprunter un montant donné
     */
    public boolean peutEmprunter(BigDecimal montant) {
        if (montant == null) return false;
        return montant.compareTo(calculerCapaciteEmprunt()) <= 0;
    }

    /**
     * Vérifie si le membre est éligible pour être gardien de clé
     */
    public boolean isEligibleGardienCle() {
        return getRoleComite() == RoleComite.AUCUN &&
                getStatut() == StatutMembre.ACTIF;
    }

    /**
     * Vérifie si le membre est éligible pour un rôle au comité
     */
    public boolean isEligibleComite() {
        return getStatut() == StatutMembre.ACTIF;
    }

    /**
     * Ajoute un achat de part
     */
    public void ajouterAchatPart(AchatPart achat) {
        if (achat != null) {
            achatsParts.add(achat);
            achat.setMembre(this);
            achat.setMembreId(this.id);
            calculerTotalEpargne();
        }
    }

    /**
     * Ajoute un prêt
     */
    public void ajouterPret(Pret pret) {
        if (pret != null) {
            prets.add(pret);
            pret.setEmprunteur(this);
            pret.setEmprunteurId(this.id);
            calculerTotalPretEnCours();
        }
    }

    /**
     * Met à jour tous les totaux
     */
    public void mettreAJourTotaux() {
        calculerNombrePartsTotal();
        calculerTotalEpargne();
        calculerTotalPretEnCours();
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + getNumeroCarte() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Membre membre = (Membre) o;
        return id != null && id.equals(membre.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}