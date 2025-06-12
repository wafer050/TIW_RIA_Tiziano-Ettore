package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.Esito;
import it.polimi.tiw.projects.beans.StudenteDati;

public class StudenteDAO {
	private Connection con;
	private int id;

	public StudenteDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

		public StudenteDati findDatiStudente() throws SQLException {
			String queryStudente = "SELECT  id, nome, cognome, matricola, mail, corso_di_laurea FROM studente  WHERE id = ?";
			try (PreparedStatement pstatementStudente = con.prepareStatement(queryStudente);) {
				pstatementStudente.setInt(1, id);
				try (ResultSet resultStudente = pstatementStudente.executeQuery();) {
					if (!resultStudente.isBeforeFirst()) // no results, credential check failed
						return null;
					else {
						resultStudente.next();
						StudenteDati studente = new StudenteDati();
						studente.setId(resultStudente.getInt("id"));
						studente.setNome(resultStudente.getString("nome"));
						studente.setCognome(resultStudente.getString("cognome"));
						studente.setMatricola(resultStudente.getInt("matricola"));
						studente.setMail(resultStudente.getString("mail"));
						studente.setCorsoLaurea(resultStudente.getString("corso_di_laurea"));
						return studente;
					}
				}
			}
		}

		//ritorna lista vuota se non ci sono corsi
		public List<Corso> findCorsiOrdDecr() throws SQLException {
		    String query = "SELECT corso_id, corso_nome FROM vw_corsi_studente WHERE studente_id = ? ORDER BY corso_nome DESC";
		    List<Corso> corsi = new ArrayList<>();

		    try (PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setInt(1, id);
		        try (ResultSet rs = ps.executeQuery()) {
		            while (rs.next()) {
		                Corso corso = new Corso();
		                corso.setId(rs.getInt("corso_id"));
		                corso.setName(rs.getString("corso_nome"));
		                corsi.add(corso);
		            }
		        }
		    }
		    return corsi;
		}
		
		public List<Appello> findAppelliByCorsoOrdDecr(int corsoID) throws SQLException {
			List<Appello> appelli = new ArrayList<Appello>();
			String query = "SELECT appello_id, appello_data FROM vw_studenti_iscritti_appello WHERE corso_id = ? and studente_id = ? ORDER BY appello_data DESC";
			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, corsoID);
				pstatement.setInt(2, this.id);
				try (ResultSet result = pstatement.executeQuery();) {
					while (result.next()) {
						Appello appello = new Appello();
						appello.setId(result.getInt("appello_id"));
						appello.setDate(result.getString("appello_data"));
						appelli.add(appello);
					}
				}
			}
			return appelli;
		}

		public Esito findEsitoAppello(int appelloID) throws SQLException {
			String query = "SELECT * FROM vw_esito_studente_appello WHERE studente_id = ? and appello_id = ?";

		    try (PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setInt(1, this.id);
		        ps.setInt(2, appelloID);
		        try (ResultSet rs = ps.executeQuery()) {
		        	if (!rs.isBeforeFirst()) // no results, credential check failed
						return null;
					else {
						rs.next();
						Esito esito = new Esito();
						esito.setIdStudente(rs.getInt("studente_id"));
						esito.setNome(rs.getString("studente_nome"));
						esito.setCognome(rs.getString("studente_cognome"));
						esito.setMatricola(rs.getInt("studente_matricola"));
						esito.setMail(rs.getString("studente_mail"));
						esito.setCorsoLaurea(rs.getString("studente_corso_di_laurea"));
						esito.setIdCorso(rs.getInt("corso_id"));
						esito.setNomeCorso(rs.getString("corso_nome"));
						esito.setIdAppello(rs.getInt("appello_id"));
						esito.setDataAppello(rs.getString("appello_data"));
						esito.setVoto(rs.getString("esito_voto"));
						esito.setStatoDiValutazione(rs.getString("esito_stato"));
						return esito;
					}
		        }
		    }
		}

		public void rifiutaVoto(int idAppello) throws SQLException {
			String query = "UPDATE esito SET stato_di_valutazione = 'rifiutato' WHERE id_studente = ? and id_appello = ?";

			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, this.id);
				pstatement.setInt(2, idAppello);

				int rows = pstatement.executeUpdate();
				if (rows == 0) {
					throw new SQLException();
				}
			}

			return;
		}

}

