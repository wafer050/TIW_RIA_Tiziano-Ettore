package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VotoDAO {
	private Connection con;
	//private int idStudente;
	private int idAppello;

	public VotoDAO(Connection connection) {
		this.con = connection;
		//this.idStudente = ids;
		//this.idAppello = ida;
	}

	public void inserisci(String voto, int idStudente, int idAppello) throws SQLException {
		String query = "UPDATE esito SET voto = ?, stato_di_valutazione = 'inserito' WHERE id_studente = ? AND id_appello = ? "
				 + "AND (stato_di_valutazione = 'inserito' OR stato_di_valutazione = 'non inserito')";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, voto);
			pstatement.setInt(2, idStudente);
			pstatement.setInt(3, idAppello);

			int rows = pstatement.executeUpdate();
			if (rows == 0) {
				throw new SQLException();
			}
		}

		return;
	}
	
	public void pubblica() throws SQLException {
		String query = "UPDATE esito "
				+ "SET stato_di_valutazione = 'pubblicato' "
				+ "WHERE stato_di_valutazione = 'inserito' AND id_appello = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idAppello);
			
			pstatement.executeUpdate();
		}
		return;
	}
}
