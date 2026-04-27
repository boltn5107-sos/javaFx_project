package com.avec.service;

import com.avec.dao.ProcesVerbalDAO;
import com.avec.model.ProcesVerbal;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ProcesVerbalService {

    private ProcesVerbalDAO pvDAO;

    public ProcesVerbalService() {
        this.pvDAO = new ProcesVerbalDAO();
    }

    public ProcesVerbal save(ProcesVerbal pv) throws SQLException {
        if (pv.getDateCreation() == null) {
            pv.setDateCreation(LocalDateTime.now());
        }
        pv.setDateModification(LocalDateTime.now());
        return pvDAO.save(pv);
    }

    public void loadRelations(ProcesVerbal pv) throws SQLException {
        if (pv != null) {
            pvDAO.loadRelations(pv);
        }
    }

    private List<ProcesVerbal> loadRelationsForList(List<ProcesVerbal> pvs) throws SQLException {
        if (pvs != null) {
            for (ProcesVerbal pv : pvs) {
                pvDAO.loadRelations(pv);
            }
        }
        return pvs;
    }

    public List<ProcesVerbal> getAll() throws SQLException {
        return loadRelationsForList(pvDAO.findAll());
    }

    public List<ProcesVerbal> getByReunionId(Long reunionId) throws SQLException {
        return loadRelationsForList(pvDAO.findByReunionId(reunionId));
    }

    public ProcesVerbal getById(Long id) throws SQLException {
        ProcesVerbal pv = pvDAO.findById(id);
        if (pv != null) {
            pvDAO.loadRelations(pv);
        }
        return pv;
    }

    public boolean delete(Long id) throws SQLException {
        return pvDAO.delete(id);
    }

    public boolean existsByReunionId(Long reunionId) throws SQLException {
        List<ProcesVerbal> pvs = pvDAO.findByReunionId(reunionId);
        return pvs != null && !pvs.isEmpty();
    }

    public ProcesVerbal getByReunionIdFirst(Long reunionId) throws SQLException {
        List<ProcesVerbal> pvs = pvDAO.findByReunionId(reunionId);
        if (pvs != null && !pvs.isEmpty()) {
            pvDAO.loadRelations(pvs.get(0));
            return pvs.get(0);
        }
        return null;
    }
}