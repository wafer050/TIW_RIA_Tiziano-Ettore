package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.Verbale;

public class DocenteDAO {
	private Connection con;
	private int id;

	public DocenteDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	public List<Corso> findCorsiInsegnatiOrdinatiCresc() throws SQLException {
		List<Corso> corsi = new ArrayList<Corso>();
		//String query = "SELECT corso.id, corso.nome FROM docente, corso WHERE docente.id = corso.id_docente AND docente.id = ? ORDER BY corso.nome ASC";
		String query = "SELECT corso_id, corso_nome FROM vw_corsi_docente WHERE docente_id = ? ORDER BY corso_nome DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Corso corso = new Corso();
					corso.setId(result.getInt("corso_id"));
					corso.setName(result.getString("corso_nome"));
					corsi.add(corso);
				}
			}
		}
		return corsi;
	}
	
	
	
	
	public List<Verbale> findVerbali() throws SQLException {
		List<Verbale> verbali = new ArrayList<Verbale>();
		String query = "SELECT verbale_id, verbale_data_creazione, verbale_ora_creazione, appello_id, appello_data, corso_id, corso_nome"
				+ " FROM vw_verbali_docente WHERE docente_id = ? ORDER BY corso_nome, appello_data ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Verbale verbale = new Verbale();
					verbale.setId(result.getInt("verbale_id"));
					verbale.setDataCreazione(result.getString("verbale_data_creazione"));
					verbale.setOraCreazione(result.getString("verbale_ora_creazione"));
					verbale.setIdAppello(result.getInt("appello_id"));
					verbale.setDataAppello(result.getString("appello_data"));
					verbale.setIdCorso(result.getInt("corso_id"));
					verbale.setNomeCorso(result.getString("corso_nome"));
					
					verbali.add(verbale);
				}
			}
		}
		return verbali;
	}
	
}
