package com.avec.dao;

import com.avec.config.DBConnection;
import com.avec.model.Reunion;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ReunionDAO {

    public void enregistrer(Reunion reunion) {

        String sql = "INSERT INTO reunion(date,type) VALUES(?,?)";

        try {

            DBConnection db = new DBConnection();
            Connection con = db.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDate(1, java.sql.Date.valueOf(reunion.getDate()));
            ps.setString(2, reunion.getType().name());

            ps.executeUpdate();

            System.out.println("Reunion enregistrée");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}