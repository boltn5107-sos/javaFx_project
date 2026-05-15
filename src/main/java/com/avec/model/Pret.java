
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
    private LocalDate dateDemande;
    private String objetPret;
    private LocalDate dateRemboursementTotal;

    // relations
    private Long emprunteurId;
    private Membre emprunteur;
    private Long reunionDecaissementId;
    private Reunion reunionDecaissement;
    private Long approuveParId;
    private Membre approuvePar;

    private List<Remboursement> remboursements;
    private List<DecaissementPret> decaissements;


    public Pret(){
        this.remboursements = new ArrayList<>();
        this.decaissements = new ArrayList<>();
        this.statut = StatutPret.EN_ATTENTE;
    }

    public Pret(Long id, String numeroPret, BigDecimal montantInitial) {
        this.id = id;
        this.numeroPret = numeroPret;
        this.montantInitial = montantInitial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getNumeroPret() {
        return numeroPret;
    }
    
    public void setNumeroPret(String text) {
        this.numeroPret = text;
    }

    public BigDecimal getMontantInitial() {
        return montantInitial;
    }

    public void setMontantInitial(BigDecimal bigDecimal) { 
        this.montantInitial = bigDecimal;
     }

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
        this.emprunteur = membre;
    }

    public void setEmprunteurId(Long id) {
        this.emprunteurId = id;
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
    
    public LocalDate getDateDemande() {
        return dateDemande;
    }
    
    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
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

   public List<Remboursement> getRemboursements() {
       return remboursements;
   }

   public void setRemboursements(List<Remboursement> remboursements) {
       this.remboursements = remboursements;
   }


    public List<DecaissementPret> getDecaissements() {
        return decaissements;
    }

    public void setDecaissements(List<DecaissementPret> decaissements) {
        this.decaissements = decaissements;
    }



    // Méthodes métier



    /**
     * Récupère le montant restant dû (capital + intérêts - remboursements)
     */
    public BigDecimal getMontantRestantDu() {
        if (montantRestantDu != null) {
            return montantRestantDu;
        }
        return getMontantTotalDu();
    }

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