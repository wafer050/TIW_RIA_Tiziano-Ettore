package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.StudenteAndEsitoAppello;

public class AppelloDAO {
	private Connection con;
	private int id;

	public AppelloDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	public List<StudenteAndEsitoAppello> findDatiAppello() throws SQLException {
		List<StudenteAndEsitoAppello> datiAppello = new ArrayList<StudenteAndEsitoAppello>();
		String query = "SELECT studente_id, studente_nome, studente_cognome, studente_matricola, studente_mail, studente_corso_di_laurea,"
				+ " esito_voto, esito_stato"
				+ " FROM vw_esito_studente_appello "
				+ "WHERE appello_id = ? "
				+ "ORDER BY FIELD( "
				+ "esito_voto,"
				+ " '', 'assente', 'rimandato', 'riprovato',"
				+ " '18', '19', '20', '21', '22', '23', '24',"
				+ " '25', '26', '27', '28', '29', '30', '30 e lode'"
				+ ") ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					StudenteAndEsitoAppello nuovo = new StudenteAndEsitoAppello();
					nuovo.setId(result.getInt("studente_id"));
					nuovo.setNome(result.getString("studente_nome"));
					nuovo.setCognome(result.getString("studente_cognome"));
					nuovo.setMatricola(result.getInt("studente_matricola"));
					nuovo.setMail(result.getString("studente_mail"));
					nuovo.setCorsoLaurea(result.getString("studente_corso_di_laurea"));
					nuovo.setVoto(result.getString("esito_voto"));
					nuovo.setStatoValutazione(result.getString("esito_stato"));
					datiAppello.add(nuovo);
				}
			}
		}
		return datiAppello;
	}
	
	public void pubblica() throws SQLException {
		String query = "UPDATE esito "
				+ "SET stato_di_valutazione = 'pubblicato' "
				+ "WHERE stato_di_valutazione = 'inserito' AND id_appello = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			
			pstatement.executeUpdate();
		}
		return;
	}
	
	//restituisce -1 se non c'è nulla da verbalizzare, altrimenti l'id del verbale creato
	public int verbalizza() throws SQLException{
		
		con.setAutoCommit(false);
		
		int idVerbale = -1;
		
		
		//conto se c'è qualche voto che si può verbalizzare
		String query1 = "SELECT COUNT(*) FROM esito WHERE id_appello = ? AND stato_di_valutazione in ('rifiutato', 'pubblicato')";
		
		//crea verbale
		String query2 = "INSERT INTO verbale (data_creazione, ora_creazione, id_appello)"
				+ " VALUES (CURDATE(), CURTIME(), ?)";
		
		//associa verbale agli studenti
		String query3 = "INSERT INTO verbalizzazione (id_studente, id_verbale) "
				+ "SELECT id_studente, ? FROM esito WHERE id_appello = ? AND stato_di_valutazione in ('rifiutato', 'pubblicato')";
		
		//cambia voto in rimandato se rifiutato,
		String query4 = "UPDATE esito "
				+ "SET voto = 'rimandato' "
				+ "WHERE stato_di_valutazione = 'rifiutato' AND id_appello = ?";
				
				
		//cambia righe da "rifiutato" e "pubblicato" a "verbalizzato"
		String query5 = "UPDATE esito "
				+ "SET stato_di_valutazione = 'verbalizzato' "
				+ "WHERE (stato_di_valutazione = 'pubblicato' OR stato_di_valutazione = 'rifiutato') AND id_appello = ?";
				
		
		try (
				 PreparedStatement pstatement1 = con.prepareStatement(query1);
				 PreparedStatement pstatement2 = con.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS);
				 PreparedStatement pstatement3 = con.prepareStatement(query3);
				 PreparedStatement pstatement4 = con.prepareStatement(query4);
				 PreparedStatement pstatement5 = con.prepareStatement(query5);
				 )
		 {
			pstatement1.setInt(1, id);
			pstatement2.setInt(1, id);
			
			ResultSet result1 = pstatement1.executeQuery();
			result1.next();
			if (!(result1.getInt(1) > 0)) {
				con.rollback();
				con.setAutoCommit(true);
				return -1;
			}
			
			pstatement2.executeUpdate();
			try (ResultSet genKey = pstatement2.getGeneratedKeys()){
				genKey.next();
				idVerbale = genKey.getInt(1);
			}
			
			pstatement3.setInt(1, idVerbale);
			pstatement3.setInt(2, id);
			pstatement4.setInt(1, id);
			pstatement5.setInt(1, id);
			
			pstatement3.executeUpdate();
			pstatement4.executeUpdate();
			pstatement5.executeUpdate();
		 } catch (SQLException e) {
			 con.rollback();
			 con.setAutoCommit(true);
			 throw e;
		 }
		
		con.commit();
		con.setAutoCommit(true);
		return idVerbale;
	}
	
	
}
