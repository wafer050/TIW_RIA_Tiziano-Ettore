package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.StudenteAndEsitoAppello;

import it.polimi.tiw.projects.beans.Verbale;

public class VerbaleDAO {
	private Connection con;
	private int id;

	public VerbaleDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	
	public Verbale getDatiVerbale() throws SQLException {
		Verbale verbale  = new Verbale();
		String query = "SELECT verbale_id, verbale_data, verbale_ora, appello_id, appello_data, corso_id, corso_nome"
				+ " FROM vw_verbale_appello_corso WHERE verbale_id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
					verbale.setId(result.getInt("verbale_id"));
					verbale.setDataCreazione(result.getString("verbale_data"));
					verbale.setOraCreazione(result.getString("verbale_ora"));
					verbale.setIdAppello(result.getInt("appello_id"));
					verbale.setDataAppello(result.getString("appello_data"));
					verbale.setIdCorso(result.getInt("corso_id"));
					verbale.setNomeCorso(result.getString("corso_nome"));
			}
		}
		return verbale;
	}
	
	public List<StudenteAndEsitoAppello> studentiVerbalizzati() throws SQLException {
		List<StudenteAndEsitoAppello> studentiDati  = new ArrayList<StudenteAndEsitoAppello>();
		String query = "SELECT studente_id, studente_nome, studente_cognome, studente_matricola, studente_mail, studente_corso_di_laurea, esito_voto"
				+ " FROM vw_esito_studente_appello WHERE studente_id in (SELECT id_studente FROM verbalizzazione WHERE id_verbale = ?) AND appello_id = (SELECT id_appello FROM verbale WHERE id = ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setInt(2, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					StudenteAndEsitoAppello studenteDati = new StudenteAndEsitoAppello();
					studenteDati.setId(result.getInt("studente_id"));
					studenteDati.setNome(result.getString("studente_nome"));
					studenteDati.setCognome(result.getString("studente_cognome"));
					studenteDati.setMatricola(result.getInt("studente_matricola"));
					studenteDati.setMail(result.getString("studente_mail"));
					studenteDati.setCorsoLaurea(result.getString("studente_corso_di_laurea"));
					studenteDati.setVoto(result.getString("esito_voto"));
					
					
					studentiDati.add(studenteDati);
			}
			}
		}
		return studentiDati;
	}
	
}
