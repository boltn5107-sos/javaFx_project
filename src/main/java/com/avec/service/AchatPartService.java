package com.avec.service;

import com.avec.dao.AchatPartDAO;
import com.avec.dao.AvecDAO;
import com.avec.dao.MembreDAO;
import com.avec.model.AchatPart;
import com.avec.model.Avec;
import com.avec.model.Membre;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AchatPartService {

    private final AchatPartDAO achatPartDAO;
    private final MembreDAO membreDAO;
    private final AvecDAO avecDAO;

    public AchatPartService() {
        this.achatPartDAO = new AchatPartDAO();
        this.membreDAO = new MembreDAO();
        this.avecDAO = new AvecDAO();
    }

    public AchatPart acheterParts(int nombreParts, long membreId, long reunionId) 
            throws SQLException, IllegalArgumentException {
        
        if (nombreParts <= 0) {
            throw new IllegalArgumentException("Le nombre de parts doit être positif");
        }
        if (membreId <= 0) {
            throw new IllegalArgumentException("ID du membre invalide");
        }

        Membre membre = membreDAO.findById(membreId);
        if (membre == null) {
            throw new IllegalArgumentException("Membre non trouvé");
        }
        if (membre.getAvecId() == null) {
            throw new IllegalArgumentException("Le membre n'appartient pas à une AVEC");
        }

        Avec avec = avecDAO.findById(membre.getAvecId());
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée");
        }
        if (avec.getPrixPart() == null) {
            throw new IllegalArgumentException("Le prix d'une part n'est pas défini pour cette AVEC");
        }

        BigDecimal montantTotal = avec.getPrixPart().multiply(BigDecimal.valueOf(nombreParts));

        AchatPart achat = new AchatPart();
        achat.setNombreParts(nombreParts);
        achat.setMontantTotal(montantTotal);
        achat.setMembreId(membreId);
        achat.setReunionId(reunionId);

        AchatPart saved = achatPartDAO.insert(achat);

        mettreAJourNombrePartsMembre(membreId);

        return saved;
    }

    public AchatPart acheterParts(int nombreParts, long membreId) throws SQLException, IllegalArgumentException {
        return acheterParts(nombreParts, membreId, 0);
    }

    public AchatPart vendreParts(int nombreParts, long membreId, long reunionId) 
            throws SQLException, IllegalArgumentException {
        
        if (nombreParts <= 0) {
            throw new IllegalArgumentException("Le nombre de parts doit être positif");
        }
        if (membreId <= 0) {
            throw new IllegalArgumentException("ID du membre invalide");
        }

        int partsActuelles = achatPartDAO.countByMembreId(membreId);
        if (nombreParts > partsActuelles) {
            throw new IllegalArgumentException("Le membre n'a que " + partsActuelles + " parts");
        }

        Membre membre = membreDAO.findById(membreId);
        if (membre == null) {
            throw new IllegalArgumentException("Membre non trouvé");
        }

       Avec avec = avecDAO.findById(membre.getAvecId());
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée");
        }
        if (avec.getPrixPart() == null) {
            throw new IllegalArgumentException("Le prix d'une part n'est pas défini");
        }

        BigDecimal montantTotal = avec.getPrixPart().multiply(BigDecimal.valueOf(nombreParts));

        AchatPart vente = new AchatPart();
        vente.setNombreParts(-nombreParts);
        vente.setMontantTotal(montantTotal.negate());
        vente.setMembreId(membreId);
        vente.setReunionId(reunionId);

        AchatPart saved = achatPartDAO.insert(vente);

        mettreAJourNombrePartsMembre(membreId);

        return saved;
    }

    public boolean modifierAchat(AchatPart achat) throws SQLException, IllegalArgumentException {
        if (achat == null || achat.getId() == null) {
            throw new IllegalArgumentException("Achat invalide");
        }

        AchatPart existing = achatPartDAO.findById(achat.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Achat non trouvé");
        }

        if (achat.getNombreParts() <= 0) {
            throw new IllegalArgumentException("Le nombre de parts doit être positif");
        }

        Membre membre = membreDAO.findById(achat.getMembreId());
        if (membre != null && membre.getAvecId() != null) {
            Avec avec = avecDAO.findById(membre.getAvecId());
            if (avec != null && avec.getPrixPart() != null) {
                BigDecimal montantTotal = avec.getPrixPart()
                        .multiply(BigDecimal.valueOf(achat.getNombreParts()));
                achat.setMontantTotal(montantTotal);
            }
        }

        boolean updated = achatPartDAO.update(achat);

        if (updated && achat.getMembreId() != null) {
            mettreAJourNombrePartsMembre(achat.getMembreId());
        }

        return updated;
    }

    public boolean supprimerAchat(long achatId) throws SQLException {
        AchatPart achat = achatPartDAO.findById(achatId);
        if (achat == null) {
            return false;
        }

        long membreId = achat.getMembreId();
        boolean deleted = achatPartDAO.delete(achatId);

        if (deleted && membreId > 0) {
            mettreAJourNombrePartsMembre(membreId);
        }

        return deleted;
    }

    public AchatPart getAchatById(long id) throws SQLException {
        return achatPartDAO.findById(id);
    }

    public List<AchatPart> getAchatsByMembre(long membreId) throws SQLException {
        return achatPartDAO.findByMembreId(membreId);
    }

    public List<AchatPart> getAchatsByReunion(long reunionId) throws SQLException {
        return achatPartDAO.findByReunionId(reunionId);
    }

    public List<AchatPart> getAchatsByAvec(long avecId) throws SQLException {
        return achatPartDAO.findByAvecId(avecId);
    }

    public int getNombrePartsMembre(long membreId) throws SQLException {
        return achatPartDAO.countByMembreId(membreId);
    }

    public BigDecimal getMontantTotalPartsMembre(long membreId) throws SQLException {
        return achatPartDAO.sumMontantByMembreId(membreId);
    }

    public BigDecimal getMontantTotalPartsAVEC(long avecId) throws SQLException {
        return achatPartDAO.sumMontantByAvecId(avecId);
    }

    public BigDecimal getPrixPart(long avecId) throws SQLException {
        Avec avec = avecDAO.findById(avecId);
        if (avec == null) {
            return BigDecimal.ZERO;
        }
        return avec.getPrixPart() != null ? avec.getPrixPart() : BigDecimal.ZERO;
    }
    
    public boolean enregistrerAchatPart(AchatPart achat) {
        if (achat == null) return false;
        if (achat.getNombreParts() == 0) return false;
        
        try {
            AchatPart result = achatPartDAO.insert(achat);
            return result != null;
        } catch (SQLException e) {
            System.err.println("Erreur enregistrement achat part: " + e.getMessage());
            return false;
        }
    }

    private void mettreAJourNombrePartsMembre(long membreId) throws SQLException {
        int nombreParts = achatPartDAO.countByMembreId(membreId);
        Membre membre = membreDAO.findById(membreId);
        if (membre != null) {
            membre.setNombreParts(nombreParts);
            membreDAO.update(membre);
        }
    }
}