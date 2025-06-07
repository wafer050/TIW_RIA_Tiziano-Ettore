package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.projects.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String queryDocente = "SELECT  id, username, nome, cognome FROM docente  WHERE username = ? AND password =?";
		//String queryStudente = "SELECT  id, username, nome, cognome, matricola, mail, corso_di_laurea FROM studente  WHERE username = ? AND password =?";
		String queryStudente = "SELECT  id, username, nome, cognome FROM studente  WHERE username = ? AND password =?";
		try (PreparedStatement pstatementDocente = con.prepareStatement(queryDocente);) {
			pstatementDocente.setString(1, usrn);
			pstatementDocente.setString(2, pwd);
			try (ResultSet resultDocente = pstatementDocente.executeQuery();) {
				if (!resultDocente.isBeforeFirst()) {// no results, credential check failed
					try (PreparedStatement pstatementStudente = con.prepareStatement(queryStudente);) {
						pstatementStudente.setString(1, usrn);
						pstatementStudente.setString(2, pwd);
						try (ResultSet resultStudente = pstatementStudente.executeQuery();) {
							if (!resultStudente.isBeforeFirst()) // no results, credential check failed
								return null;
							else {
								resultStudente.next();
								User user = new User();
								user.setId(resultStudente.getInt("id"));
								user.setRole("studente");
								user.setUsername(resultStudente.getString("username"));
								user.setNome(resultStudente.getString("nome"));
								user.setCognome(resultStudente.getString("cognome"));
								//user.setMatricola(resultStudente.getInt("matricola"));
								//user.setMail(resultStudente.getString("mail"));
								//user.setCorsoLaurea(resultStudente.getString("corso_di_laurea"));
								return user;
							}
						}
					}
				}
				else {
					resultDocente.next();
					User user = new User();
					user.setId(resultDocente.getInt("id"));
					user.setRole("docente");
					user.setUsername(resultDocente.getString("username"));
					user.setNome(resultDocente.getString("nome"));
					user.setCognome(resultDocente.getString("cognome"));
					return user;
				}
			}
		}
	}
}
