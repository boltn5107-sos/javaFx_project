package com.avec.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.model.Utilisateur;

public class UtilisateurDao {

	// Insérer un utilisateur dans la base
	public boolean ajouter(Utilisateur utilisateur) {

		String sql = "INSERT INTO utilisateur(nom, prenom, email, motDePasse, telephone) VALUES(?,?,?,?,?) ";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, utilisateur.getNom());
			ps.setString(2, utilisateur.getPrenom());
			ps.setString(3, utilisateur.getEmail());
			ps.setString(4, utilisateur.getMotDePasse());
			ps.setString(5, utilisateur.getTelephone());

			int u = ps.executeUpdate();

			if (u > 0) {
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (generatedKeys.next()) {
					utilisateur.setId(generatedKeys.getLong(1));
				}
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Erreur lors de la sauvegarde de l'utilisateur: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	// Chercher un utilisateur par son id

	public Utilisateur chercherId(Long id) {

		String sql = "SELECT * FROM utilisateur WHERE id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSetToUtilisateur(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}

		return null;
	}

	// Chercher l'email et le mot de passe (pour la connexion)

	public Utilisateur chercherEmailEtMotDePasse(String email, String password) {

		String sql = "SELECT * FROM utilisateur WHERE email = ? AND motDePasse = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return mapResultSetToUtilisateur(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	// Lister les utilisateur
	public List<Utilisateur> lister() {

		List<Utilisateur> utilisateurs = new ArrayList<>();

		String sql = "SELECT * FROM utilisateur ORDER BY nom, prenom";

		try (Connection con = DBConnection.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				utilisateurs.add(mapResultSetToUtilisateur(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return utilisateurs;
	}

	// Modifier utilisateur

	public boolean modifier(Utilisateur utilisateur) {

		String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, motDePasse = ?, telephone = ? WHERE id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, utilisateur.getNom());
			ps.setString(2, utilisateur.getPrenom());
			ps.setString(3, utilisateur.getEmail());
			ps.setString(4, utilisateur.getMotDePasse());
			ps.setString(5, utilisateur.getTelephone());
			ps.setLong(6, utilisateur.getId());

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Supprimer un utilisateur

	public boolean spprimer(Long id) {

		String sql = "DELETE FROM utilisateur WHERE id = ?";

		try (Connection con = DBConnection.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setLong(1, id);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Mapping ResultSet -> Utilisateur
	private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setId(rs.getLong("id"));
		utilisateur.setNom(rs.getString("nom"));
		utilisateur.setPrenom(rs.getString("prenom"));
		utilisateur.setEmail(rs.getString("email"));
		utilisateur.setMotDePasse(rs.getString("motDePasse"));
		utilisateur.setTelephone(rs.getString("telephone"));
		return utilisateur;
	}

}
