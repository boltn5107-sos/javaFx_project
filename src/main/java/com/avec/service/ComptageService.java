package com.avec.service;

import com.avec.dao.ComptageDAO;
import com.avec.model.Comptage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ComptageService {

    private ComptageDAO comptageDAO;

    public ComptageService() {
        this.comptageDAO = new ComptageDAO();
    }

    public Comptage save(Comptage comptage) throws SQLException {
        if (comptage.getCreatedAt() == null) {
            comptage.setCreatedAt(LocalDateTime.now());
        }
        comptage.setUpdatedAt(LocalDateTime.now());
        return comptageDAO.save(comptage);
    }

    public Comptage getById(Long id) throws SQLException {
        return comptageDAO.findById(id);
    }

    public List<Comptage> getByAvecId(Long avecId) throws SQLException {
        return comptageDAO.findByAvecId(avecId);
    }

    public List<Comptage> getAll() throws SQLException {
        return comptageDAO.findAll();
    }

    public boolean delete(Long id) throws SQLException {
        return comptageDAO.delete(id);
    }

    public Comptage getLastComptage(Long avecId) throws SQLException {
        return comptageDAO.findLastByAvecId(avecId);
    }

    public boolean confirmerComptage(Long id) throws SQLException {
        Comptage comptage = comptageDAO.findById(id);
        if (comptage != null) {
            comptage.setEstConfirme(true);
            comptageDAO.save(comptage);
            return true;
        }
        return false;
    }
}