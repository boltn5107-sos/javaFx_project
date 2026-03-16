package com.avec.service;

import com.avec.dao.ReunionDAO;
import com.avec.model.Reunion;

public class ReunionService {

    private ReunionDAO reunionDAO = new ReunionDAO();

    public void creerReunion(Reunion reunion){

        reunionDAO.enregistrer(reunion);

    }
}