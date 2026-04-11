package com.avec.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.enums.RoleComite;
import com.avec.enums.RoleDetenteurCle;
import com.avec.enums.StatutMembre;
import com.avec.model.Membre;

/**
 * DAO pour la gestion des membres dans la base de données
 */
public class MembreDAO {

    /**
     * Insère un nouveau membre
     */
    public Membre insert(Membre membre) throws SQLException {
        String sql = "INSERT INTO membre (nom, prenom, numeroCarte, estActif,dateAdhesion, " +
                "  avec_id, roleComite, roleCle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getNumeroCarte());
            
         //  Correction: Convertir StatutMembre en boolean
            boolean estActif = (membre.getEstActif() == StatutMembre.ACTIF);
            stmt.setBoolean(4, estActif);
            stmt.setDate(5, Date.valueOf(membre.getDateAdhesion()));
            stmt.setLong(6, membre.getAvecId());
            stmt.setString(7, membre.getRoleComite().name());
            stmt.setString(8, membre.getRoleCle().name());
            
            

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du membre a échoué");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    membre.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création du membre a échoué, aucun ID obtenu");
                }
            }
        }
        return membre;
    }

    /**
     * Met à jour un membre existant
     */
    public boolean update(Membre membre) throws SQLException {
        String sql = "UPDATE membre SET nom = ?, prenom = ?, numeroCarte = ?, estActif = ?,dateAdhesion = ?," + 
        		 "avec_id = ?, roleComite = ?, roleCle = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        	stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getNumeroCarte());
            stmt.setBoolean(4, membre.getEstActif() == StatutMembre.ACTIF );
            stmt.setDate(5, Date.valueOf(membre.getDateAdhesion()));
            stmt.setLong(6, membre.getAvecId());
            stmt.setString(7, membre.getRoleComite().name());
            stmt.setString(8, membre.getRoleCle().name());
            stmt.setLong(9, membre.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime un membre par son ID
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM membre WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve un membre par son ID
     */
    public Membre findById(long id) throws SQLException {
        String sql = "SELECT m.*, u.email, u.motDePasse, u.telephone " +
                "FROM membre m " +
                "LEFT JOIN utilisateur u ON m.id = u.id " +
                "WHERE m.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve tous les membres d'une AVEC
     */
    public List<Membre> findByAvecId(long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT m.id, m.nom, m.prenom, m.numeroCarte, m.estActif, m.dateAdhesion, " +
                "m.avec_id, m.roleComite, m.roleCle, " +
                "COALESCE((SELECT SUM(a.nombreParts) FROM achatsparts a WHERE a.membre_id = m.id), 0) AS totalParts, " +
                "u.telephone " +
                "FROM membre m " +
                "LEFT JOIN utilisateur u ON u.email = CONCAT(m.numeroCarte, '@membre.avec.com') " +
                "WHERE m.avec_id = ? ORDER BY m.nom, m.prenom";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                   Membre membre = mapResultSetToMembres(rs);
                 // ✅ Ajouter le téléphone
                    membre.setTelephone(rs.getString("telephone"));
                    membres.add(membre);
                }
            }
        }
        return membres;
    }
    
    /**
     * Cherche un membre par sa carte et son mot de passe (jointure avec utilisateur)
     */
    /**
     * Cherche un membre par sa carte et son mot de passe (jointure avec utilisateur)
     */
    public Membre chercherParCarteEtMotDePasse(String numeroCarte, String motDePasse) {
        System.out.println(">>> DAO: chercherParCarteEtMotDePasse");
        System.out.println(">>> Carte: " + numeroCarte);
        System.out.println(">>> Mot de passe: " + motDePasse);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT m.id, m.nom, m.prenom, m.numeroCarte, m.estActif, m.dateAdhesion, " +
                         "m.avec_id, m.roleComite, m.roleCle " +
                         "FROM membre m " +
                         "INNER JOIN utilisateur u ON u.email = CONCAT(m.numeroCarte, '@membre.avec.com') " +
                         "WHERE m.numeroCarte = ? AND u.motDePasse = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, numeroCarte);
            pstmt.setString(2, motDePasse);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println(">>> Membre trouvé!");
                return mapResultSetToMembre(rs);
            } else {
                System.out.println(">>> Aucun membre trouvé avec cette carte et mot de passe");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur chercherParCarteEtMotDePasse: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * Trouve un membre par son numéro de carte
     */
    public Membre findByNumeroCarte(String numeroCarte) throws SQLException {
        String sql = "SELECT * FROM membre WHERE numeroCarte = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroCarte);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }
        }
        return null;
    }

    /**
     * Trouve les membres par rôle au comité
     */
    public List<Membre> findByRoleComite(RoleComite role) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membre WHERE roleComite = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Trouve les gardiens de clés d'une AVEC
     */
    public List<Membre> findGardiensCles(long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membre WHERE avec_id = ? AND roleCle != 'AUCUN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }
    
    

    /**
     * ✅ Met à jour le rôle de clé d'un membre
     */
    public boolean updateRoleCle(long membreId, String role) throws SQLException {
        String sql = "UPDATE membre SET roleCle = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            stmt.setLong(2, membreId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Trouve le comité de gestion d'une AVEC
     */
    public List<Membre> findComiteGestion(long avecId) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membre WHERE avec_id = ? AND roleComite != 'AUCUN'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }

    /**
     * Compte le nombre de membres actifs dans une AVEC
     */
    public int countActifsByAvecId(long avecId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM membre WHERE avec_id = ? AND estActif = 'true'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Met à jour le rôle au comité
     */
    public boolean updateRoleComite(long membreId, RoleComite role) throws SQLException {
        String sql = "UPDATE membre SET roleComite = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());
            stmt.setLong(2, membreId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour le rôle de gardien de clé
     */
    public boolean updateRoleCle(long membreId, RoleDetenteurCle role) throws SQLException {
        String sql = "UPDATE membre SET roleCle = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());
            stmt.setLong(2, membreId);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Réinitialise tous les rôles du comité pour une AVEC
     */
    public boolean resetAllRolesComite(long avecId) throws SQLException {
        String sql = "UPDATE membre SET roleComite = 'AUCUN' WHERE avec_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Réinitialise tous les rôles de gardien de clé pour une AVEC
     */
    public boolean resetAllRolesCle(long avecId) throws SQLException {
        String sql = "UPDATE membre SET roleCle = 'AUCUN' WHERE avec_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, avecId);
            return stmt.executeUpdate() > 0;
        }
    }

   
    

    /**
     * Recherche des membres par nom
     */
    public List<Membre> searchByNom(long avecId, String recherche) throws SQLException {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membre WHERE avec_id = ? AND (nom LIKE ? OR prenom LIKE ? OR nom_complet LIKE ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + recherche + "%";
            stmt.setLong(1, avecId);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        }
        return membres;
    }
    /*
    * Récupère TOUS les membres de toutes les AVEC
    */
   public List<Membre> findAll() throws SQLException {
       List<Membre> membres = new ArrayList<>();
       String sql = "SELECT * FROM membre ORDER BY nom, prenom";

       try (Connection conn = DBConnection.getConnection();
    		   Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

           while (rs.next()) {
               membres.add(mapResultSetToMembre(rs));
           }
       }
       return membres;
   }

   /**
    * Compte le nombre total de membres (toutes AVEC confondues)
    */
   public int countAll() throws SQLException {
       String sql = "SELECT COUNT(*) FROM membre";

       try (Connection conn = DBConnection.getConnection();
    		   Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

           if (rs.next()) {
               return rs.getInt(1);
           }
       }
       return 0;
   }

   /**
    * Compte le nombre de membres actifs (toutes AVEC confondues)
    */
   public int countActifs() throws SQLException {
       String sql = "SELECT COUNT(*) FROM membre WHERE estActif = 'true'";

       try (Connection conn = DBConnection.getConnection();
    		   Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
    	   
    	  

           if (rs.next()) {
        	   
               return rs.getInt(1);
           }
       }
       return 0;
   }

   /**
    * Calcule le total de l'épargne de tous les membres
    */
   public BigDecimal sumTotalEpargne() throws SQLException {
       String sql = "SELECT COALESCE(SUM(total_epargne), 0) FROM membre";

       try (Connection conn = DBConnection.getConnection();
    		   Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

           if (rs.next()) {
               return rs.getBigDecimal(1);
           }
       }
       return BigDecimal.ZERO;
   }
   
   /**
    * Calcule le total de l'épargne de tous les membres
    */
   public BigDecimal sumTotalPretEnCours() throws SQLException {
       String sql = "SELECT COALESCE(SUM(montantRestantDu), 0) FROM pret WHERE statut <> 'REMBOURSE' ";

       try (Connection conn = DBConnection.getConnection();
    		   Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

           if (rs.next()) {
        	  
               return rs.getBigDecimal(1);
           }
       }
       return BigDecimal.ZERO;
   }

 

   /**
    * Recherche des membres par nom dans toutes les AVEC
    */
   public List<Membre> searchByNomGlobal(String recherche) throws SQLException {
       List<Membre> membres = new ArrayList<>();
       String sql = "SELECT * FROM membre WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";

       try (Connection conn = DBConnection.getConnection();
    		   PreparedStatement stmt = conn.prepareStatement(sql)) {
           stmt.setString(1, "%" + recherche + "%");
           stmt.setString(2, "%" + recherche + "%");

           try (ResultSet rs = stmt.executeQuery()) {
               while (rs.next()) {
                   membres.add(mapResultSetToMembre(rs));
               }
           }
       }
       return membres;
   }

   
  

    /**
     * Map un ResultSet vers un objet Membre
     */
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre membre = new Membre();

        membre.setId(rs.getLong("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setNumeroCarte(rs.getString("numeroCarte"));
        // Correction: Convertir boolean en StatutMembre
        boolean estActif = rs.getBoolean("estActif");
        membre.setEstActif(estActif ? StatutMembre.ACTIF : StatutMembre.INACTIF);
        Date dateAdhesion = rs.getDate("dateAdhesion");
        if (dateAdhesion != null) {
            membre.setDateAdhesion(dateAdhesion.toLocalDate());
        }
        membre.setAvecId(rs.getLong("avec_id"));
        
        String roleComiteStr = rs.getString("roleComite");
        if (roleComiteStr != null) {
            membre.setRoleComite(RoleComite.valueOf(roleComiteStr));
        }
        
        String roleCleStr = rs.getString("roleCle");
        if (roleCleStr != null) {
            membre.setRoleCle(RoleDetenteurCle.valueOf(roleCleStr));
        }
   
        

        return membre;
    }
    
    private Membre mapResultSetToMembres(ResultSet rs) throws SQLException {
        Membre membre = new Membre();

        membre.setId(rs.getLong("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setNumeroCarte(rs.getString("numeroCarte"));
        
        // ✅ Récupérer le téléphone
        try {
            membre.setTelephone(rs.getString("telephone"));
        } catch (SQLException e) {
            // La colonne telephone n'existe pas dans ce ResultSet
            membre.setTelephone(null);
        }
        // Correction: Convertir boolean en StatutMembre
        boolean estActif = rs.getBoolean("estActif");
        membre.setEstActif(estActif ? StatutMembre.ACTIF : StatutMembre.INACTIF);
        Date dateAdhesion = rs.getDate("dateAdhesion");
        if (dateAdhesion != null) {
membre.setDateAdhesion(dateAdhesion.toLocalDate());
        }
        membre.setAvecId(rs.getLong("avec_id"));
        
        try {
            membre.setNombreParts(rs.getInt("totalParts"));
        } catch (SQLException e) {
            membre.setNombreParts(0);
        }
        
        String roleComiteStr = rs.getString("roleComite");
        if (roleComiteStr != null) {
            membre.setRoleComite(RoleComite.valueOf(roleComiteStr));
        }
        
        String roleCleStr = rs.getString("roleCle");
        if (roleCleStr != null) {
            membre.setRoleCle(RoleDetenteurCle.valueOf(roleCleStr));
        }
        
        return membre;
    }
}