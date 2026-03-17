package com.avec.service;

import com.avec.dao.PretDAO;
import com.avec.model.Pret;

public class PretService {

    private PretDAO pretDAO = new PretDAO();

    public void creerPret(Pret pret){

        pretDAO.enregistrer(pret);

    }
}
