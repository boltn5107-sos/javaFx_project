package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.AchatPart;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AchatPartDAO {

    public void enregistrer(AchatPart achat) {

        String sql = "INSERT INTO achat_part(nombreParts,montantTotal) VALUES(?,?)";

        try {

            DBConnection db = new DBConnection();
            Connection con = db.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, achat.getNombreParts());
            ps.setBigDecimal(2, achat.getMontantTotal());

            ps.executeUpdate();

            System.out.println("Achat de parts enregistré");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
