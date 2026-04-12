package com.avec.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.model.TypeInfraction;

public class TypeInfractionDAO {

    private Connection connection;

    public TypeInfractionDAO() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère tous les types d'infractions
     */
    public List<TypeInfraction> findAll() throws SQLException {
        List<TypeInfraction> types = new ArrayList<>();
        String sql = "SELECT * FROM type_infraction WHERE est_actif = TRUE ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                types.add(mapResultSetToTypeInfraction(rs));
            }
        }
        return types;
    }

    /**
     * Récupère un type d'infraction par son ID
     */
    public TypeInfraction findById(Long id) throws SQLException {
        String sql = "SELECT * FROM type_infraction WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTypeInfraction(rs);
                }
            }
        }
        return null;
    }

    /**
     * Récupère un type d'infraction par son nom
     */
    public TypeInfraction findByNom(String nom) throws SQLException {
        String sql = "SELECT * FROM type_infraction WHERE nom = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nom);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTypeInfraction(rs);
                }
            }
        }
        return null;
    }

    /**
     * Map ResultSet vers TypeInfraction
     */
    private TypeInfraction mapResultSetToTypeInfraction(ResultSet rs) throws SQLException {
        TypeInfraction type = new TypeInfraction();
        type.setId(rs.getLong("id"));
        type.setNom(rs.getString("nom"));
        type.setDescription(rs.getString("description"));
        type.setMontantDefaut(rs.getBigDecimal("montant_defaut"));
        type.setEstActif(rs.getBoolean("est_actif"));
        return type;
    }
}