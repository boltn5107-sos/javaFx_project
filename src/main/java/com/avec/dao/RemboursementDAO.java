package com.avec.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.avec.config.DBConnection;
import com.avec.model.Pret;
import com.avec.model.Remboursement;
import com.avec.model.Reunion;

public class RemboursementDAO {

	private PretDAO pretDao;
	private ReunionDAO reunionDao;

	public RemboursementDAO() {
		this.pretDao = new PretDAO();
		this.reunionDao = new ReunionDAO();
	}

	// Enregistrer un remboursement

	public boolean enregistrer(Remboursement remboursement) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();

			String sql = "INSERT INTO remboursements (montant, pret_id, reunion_id) VALUES(?,?,?)";

			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setBigDecimal(1, remboursement.getMontant());
			pstmt.setLong(2, remboursement.getPretId());
			pstmt.setLong(3, remboursement.getReunionId());

			int rows = pstmt.executeUpdate();

			if (rows == 0)
				return false;

			rs = pstmt.getGeneratedKeys();

			while (rs.next()) {
				remboursement.setId(rs.getLong(1));
			}

			// Mettre à jour le montant restant du prêt

			mettreAJourMontantPret(conn, remboursement);

			return true;

		} catch (SQLException e) {
			System.err.println("Erreur dans enregistrer(): " + e.getMessage());
			e.printStackTrace();

			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Méthode pour mettre à jour le montant restant du prêt
	private void mettreAJourMontantPret(Connection conn, Remboursement remboursement) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			// Récupérerle prêt

			Pret pret = pretDao.findPretById(remboursement.getPretId());
			if (pret == null)
				return;

			// Calculer le nouveau montant restant dû
			BigDecimal montantRembourse = remboursement.getMontant();
			BigDecimal montantRestantActuel = pret.getMontantRestantDu();
			BigDecimal nouveauMontantRestant = montantRestantActuel.subtract(montantRembourse);

			// Mettre à jour le montant restant
			String sqlUpdate = "UPDATE pret SET montantRestantDu = ? WHERE id = ?";
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setBigDecimal(1, nouveauMontantRestant);
			pstmt.setLong(2, remboursement.getPretId());
			pstmt.executeUpdate();
			pstmt.close();

			// Vérifier si le prêt est entièrement remboursé
			if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) <= 0) {
				String sqlStatus = "UPDATE pret SET statut = 'REMBOURSE', dateRemboursement = CURRENT_DATE WHERE id =?";
				pstmt = conn.prepareStatement(sqlStatus);
				pstmt.setLong(1, remboursement.getPretId());
				pstmt.executeUpdate();
			}

		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();

		}
	}

	// Chercher par Id

	public Remboursement chercherId(Long id) {
		if (id == null)
			return null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();

			String sql = "SELECT * FROM remboursements WHERE id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return mapResultSetToRemboursement(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erreur dans chercherId(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// Lister tous les remboursements
	public List<Remboursement> lister() {
		List<Remboursement> remboursements = new ArrayList<>();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();

			String sql = "SELECT * FROM remboursements ORDER BY id DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				remboursements.add(mapResultSetToRemboursement(rs));
			}

		} catch (SQLException e) {
			System.err.println("Erreur dans lister(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return remboursements;

	}

	// Lister par prêt
	public List<Remboursement> listerParPretId(Long pretId) {
		List<Remboursement> remboursements = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();

			String sql = "SELECT * FROM remboursements WHERE pret_id = ? ORDER BY id DESC";
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, pretId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				remboursements.add(mapResultSetToRemboursement(rs));
			}

		} catch (SQLException e) {
			System.err.println("Erreur dans listerParId(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return remboursements;

	}

	// Lister par reunion id
	public List<Remboursement> listerParReunionId(Long reunionId) {
		List<Remboursement> remboursements = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();

			String sql = "SELECT * FROM remboursements WHERE reunion_id = ? ORDER BY id DESC";
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, reunionId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				remboursements.add(mapResultSetToRemboursement(rs));
			}

		} catch (SQLException e) {
			System.err.println("Erreur dans listerParId(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return remboursements;

	}

	// Supprimer
	public boolean supprimer(Long id) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBConnection.getConnection();

			String sql = "DELETE * FROM remboursements WHERE id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, id);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			System.err.println("Erreur dans supprimer(): " + e.getMessage());
			e.printStackTrace();
			return false;

		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Total des remboursements pour un prêt

	public BigDecimal totalRemboursementsParPret(Long pretId) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = DBConnection.getConnection();

			String sql = "SELECT COALESCE(SUM(montant), 0) as total FROM remboursements WHERE pret_id = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, pretId);
			rs = pstmt.executeQuery();

			if (rs.next()) {

				return rs.getBigDecimal("total");
			}

		} catch (SQLException e) {
			System.err.println("Erreur dans totalRemboursementsParPret(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return BigDecimal.ZERO;
	}

	// Compter
	public int compter() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = DBConnection.getConnection();

			String sql = "SELECT COUNT(*) FROM remboursements";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			System.err.println("Erreur compter(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	// Mapping Resultset to Rembousement
	private Remboursement mapResultSetToRemboursement(ResultSet rs) throws SQLException {
		Remboursement remboursement = new Remboursement();
		remboursement.setId(rs.getLong("id"));
		remboursement.setMontant(rs.getBigDecimal("montant"));

		// Récupérer les IDs
		Long pretId = rs.getLong("pret_id");
		if (!rs.wasNull()) {
			remboursement.setPretId(pretId);
			// Charger le prêt (optionnel - peut-être chargé à la demande)
			Pret pret = pretDao.findPretById(pretId);
			remboursement.setPret(pret);
		}
		
//		Long reunionId = rs.getLong("reunion_id");
//		if (!rs.wasNull()) {
//			remboursement.setPretId(reunionId);
//			// Charger le prêt (optionnel - peut-être chargé à la demande)
//			Reunion reunion = reunionDao.findReunionById(reunionId);
//			remboursement.setReunion(reunion);
//		}

		return remboursement;

	}

}
