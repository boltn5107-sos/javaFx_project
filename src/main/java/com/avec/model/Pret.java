
package com.avec.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.avec.enums.StatutPret;

public class Pret {

    private Long id;
    private String numeroPret;
    private BigDecimal montantInitial;
    private BigDecimal fraisServiceMensuel;
    private BigDecimal montantRestantDu;
    private int dureeEnSemaines;
    private StatutPret statut;

    private LocalDate dateEcheance;
    private String objetPret;
    private LocalDate dateRemboursementTotal;

    // relations
    private Long emprunteurId;
    private Membre emprunteur;
    private Long reunionDecaissementId;
    private Reunion reunionDecaissement;
    private Long approuveParId;
    private Membre approuvePar;

    //private List<Remboursement> remboursements;
    private List<DecaissementPret> decaissements;


    public Pret(){
        //this.remboursements = new ArrayList<>();
        this.decaissements = new ArrayList<>();
        this.statut = StatutPret.ACTIF;
    }

    public Pret(Long id, String numeroPret, BigDecimal montantInitial) {
        this.id = id;
        this.numeroPret = numeroPret;
        this.montantInitial = montantInitial;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setNumeroPret(String text) { this.numeroPret = numeroPret; }
    public String getNumeroPret() {
        return numeroPret;
    }

    public BigDecimal getMontantInitial() {
        return montantInitial;
    }

    public void setMontantInitial(BigDecimal bigDecimal) { this.montantInitial = montantInitial; }

    public StatutPret getStatut() {
        return statut;
    }

    public void setStatut(StatutPret statut) {
        this.statut = statut;
    }



    /**
     * Calcule le montant total dû avec les intérêts
     */
    public BigDecimal getMontantTotalDu() {

        if (montantInitial == null || fraisServiceMensuel == null) return BigDecimal.ZERO;

        // Nombre de mois (arrondi supérieur)
        int nombreMois = (int) Math.ceil(dureeEnSemaines / 4.0);

        // Total des intérêts
        BigDecimal totalInterets = fraisServiceMensuel.multiply(BigDecimal.valueOf(nombreMois));

        return montantInitial.add(totalInterets);
    }

    public void setEmprunteur(Membre membre) {
    }

    public void setEmprunteurId(Long id) {
    }


    public void setMontantRestantDu(BigDecimal montantRestantDu) {
        this.montantRestantDu = montantRestantDu;
    }

    public int getDureeEnSemaines() {
        return dureeEnSemaines;
    }

    /**
     * Récupère la date de décaissement (via la réunion associée)
     */
    public LocalDate getDateDecaissement() {
        if (reunionDecaissement != null) {
            return reunionDecaissement.getDate();
        }
        return null;
    }


    public void setDureeEnSemaines(int dureeEnSemaines) {
        this.dureeEnSemaines = dureeEnSemaines;
        if (this.dateEcheance == null && this.getDateDecaissement() != null) {
            this.dateEcheance = this.getDateDecaissement().plusWeeks(dureeEnSemaines);
        }


    }


    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getObjetPret() {
        return objetPret;
    }

    public void setObjetPret(String objetPret) {
        this.objetPret = objetPret;
    }

    public LocalDate getDateRemboursementTotal() {
        return dateRemboursementTotal;
    }

    public void setDateRemboursementTotal(LocalDate dateRemboursementTotal) {
        this.dateRemboursementTotal = dateRemboursementTotal;
    }


    public Long getEmprunteurId() {
        return emprunteurId;
    }

    public Membre getEmprunteur() {
        return emprunteur;
    }

    public Long getReunionDecaissementId() {
        return reunionDecaissementId;
    }

    public void setReunionDecaissementId(Long reunionDecaissementId) {
        this.reunionDecaissementId = reunionDecaissementId;
    }

    public Reunion getReunionDecaissement() {
        return reunionDecaissement;
    }

    public void setReunionDecaissement(Reunion reunionDecaissement) {
        this.reunionDecaissement = reunionDecaissement;
        if (reunionDecaissement != null) {
            this.reunionDecaissementId = reunionDecaissement.getId();
        }
    }

    public Long getApprouveParId() {
        return approuveParId;
    }

    public void setApprouveParId(Long approuveParId) {
        this.approuveParId = approuveParId;
    }

    public Membre getApprouvePar() {
        return approuvePar;
    }

    public void setApprouvePar(Membre approuvePar) {
        this.approuvePar = approuvePar;
        if (approuvePar != null) {
            this.approuveParId = approuvePar.getId();
        }
    }

//    public List<Remboursement> getRemboursements() {
//        return remboursements;
//    }
//
//    public void setRemboursements(List<Remboursement> remboursements) {
//        this.remboursements = remboursements;
//    }


    public List<DecaissementPret> getDecaissements() {
        return decaissements;
    }

    public void setDecaissements(List<DecaissementPret> decaissements) {
        this.decaissements = decaissements;
    }



    // Méthodes métier



    /**
     * Calcule le montant total dû avec les intérêts
     */
    public BigDecimal getMontantRestantDu() {
        if (montantInitial == null || fraisServiceMensuel == null) return BigDecimal.ZERO;

        // Nombre de mois (arrondi supérieur)
        int nombreMois = (int) Math.ceil(dureeEnSemaines / 4.0);

        // Total des intérêts
        BigDecimal totalInterets = fraisServiceMensuel.multiply(BigDecimal.valueOf(nombreMois));

        return montantInitial.add(totalInterets);
    }

    /**
     * Enregistre un remboursement
     */
//    public Remboursement rembourser(BigDecimal montant, Reunion reunion) {
//        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Le montant du remboursement doit être positif");
//        }
//
//        if (statut == StatutPret.REMBOURSE) {
//            throw new IllegalStateException("Ce prêt est déjà entièrement remboursé");
//        }
//
//        if (montant.compareTo(montantRestantDu) > 0) {
//            throw new IllegalArgumentException("Le montant remboursé (" + montant +
//                    ") dépasse le solde dû (" + montantRestantDu + ")");
//        }
//
//        // Créer le remboursement
//        Remboursement remboursement = new Remboursement();
//        remboursement.setMontant(montant);
//        remboursement.setReunion(reunion);
//        remboursement.setPret(this);
//        remboursement.setPretId(this.id);
//
//        // Mettre à jour le montant restant dû
//        BigDecimal nouveauSolde = montantRestantDu.subtract(montant);
//        this.montantRestantDu = nouveauSolde;
//
//        // Vérifier si le prêt est entièrement remboursé
//        if (this.montantRestantDu.compareTo(BigDecimal.ZERO) == 0) {
//            this.statut = StatutPret.REMBOURSE;
//            this.dateRemboursementTotal = reunion.getDate();
//        }
//
//        remboursements.add(remboursement);
//        return remboursement;
//    }

    /**
     * Vérifie si le prêt est en retard
     */
    public boolean estEnRetard() {
        if (statut == StatutPret.REMBOURSE) return false;
        if (dateEcheance == null) return false;
        return LocalDate.now().isAfter(dateEcheance);
    }

    /**
     * Calcule les frais mensuels à payer
     */
    public BigDecimal calculerFraisMensuels() {
        if (montantInitial == null || fraisServiceMensuel == null) return BigDecimal.ZERO;
        return montantInitial.multiply(fraisServiceMensuel)
                .divide(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return "Pret{" +
                "numeroPret='" + numeroPret + '\'' +
                ", montantInitial=" + montantInitial +
                ", montantRestantDu=" + montantRestantDu +
                ", statut=" + statut +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pret pret = (Pret) o;
        return id != null && id.equals(pret.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public BigDecimal getFraisServiceMensuel() {
        return fraisServiceMensuel;
    }

    public void setFraisServiceMensuel(BigDecimal fraisServiceMensuel) {
        this.fraisServiceMensuel = fraisServiceMensuel;
    }

}