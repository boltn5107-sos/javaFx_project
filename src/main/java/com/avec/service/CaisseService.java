package com.avec.service;

import com.avec.dao.CaisseDAO;
import com.avec.dao.AvecDAO;
import com.avec.model.Caisse;
import com.avec.model.Avec;

import java.sql.SQLException;
import java.util.List;

/**
 * Service pour la gestion des caisses
 */
public class CaisseService {

    private final CaisseDAO caisseDAO;
    private final AvecDAO avecDAO;

    public CaisseService() {
        this.caisseDAO = new CaisseDAO();
        this.avecDAO = new AvecDAO();
    }

    /**
     * Récupère la caisse d'une AVEC
     */
    public Caisse getCaisseByAvecId(long avecId) throws SQLException {
        // Vérifier que l'AVEC existe
        Avec avec = avecDAO.findById(avecId);
        if (avec == null) {
            throw new IllegalArgumentException("AVEC non trouvée avec l'ID: " + avecId);
        }

        Caisse caisse = caisseDAO.findByAvecId(avecId);

        // Si la caisse n'existe pas, la créer automatiquement
        if (caisse == null) {
            caisse = caisseDAO.creerCaissePourAvec(avecId);
        }

        return caisse;
    }

    /**
     * Récupère une caisse par son ID
     */
    public Caisse getCaisseById(long id) throws SQLException {
        return caisseDAO.findById(id);
    }

    /**
     * Récupère toutes les caisses
     */
    public List<Caisse> getAllCaisses() throws SQLException {
        return caisseDAO.findAll();
    }

    /**
     * Génère un nouveau code de sécurité
     */
    public String genererNouveauCodeSecurite(long caisseId) throws SQLException {
        Caisse caisse = caisseDAO.findById(caisseId);
        if (caisse == null) {
            throw new IllegalArgumentException("Caisse non trouvée");
        }

        String nouveauCode = genererCodeSecurite();
        caisseDAO.updateCodeSecurite(caisseId, nouveauCode);
        return nouveauCode;
    }

    /**
     * Vérifie le code de sécurité
     */
    public boolean verifierCodeSecurite(long caisseId, String code) throws SQLException {
        Caisse caisse = caisseDAO.findById(caisseId);
        return caisse != null && caisse.getCodeSecurite().equals(code);
    }

    /**
     * Supprime une caisse
     */
    public boolean supprimerCaisse(long id) throws SQLException {
        return caisseDAO.delete(id);
    }

    /**
     * Génère un code de sécurité aléatoire
     */
    private String genererCodeSecurite() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    /**
     * Vérifie si une AVEC a une caisse
     */
    public boolean hasCaisse(long avecId) throws SQLException {
        return caisseDAO.existePourAvec(avecId);
    }
}