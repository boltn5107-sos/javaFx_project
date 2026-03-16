package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Pret;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PretDAO {

    public void enregistrer(Pret pret) {

        String sql = "INSERT INTO pret(numeroPret,montantInitial) VALUES(?,?)";

        try {

            DBConnection db = new DBConnection();
            Connection con = db.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, pret.getNumeroPret());
            ps.setBigDecimal(2, pret.getMontantInitial());

            ps.executeUpdate();

            System.out.println("Pret enregistré");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}