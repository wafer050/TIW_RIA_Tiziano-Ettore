package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.User;

public class CorsoDAO {
	private Connection con;
	private int id;

	public CorsoDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}
	
	public List<Appello> findAppelli() throws SQLException {
		List<Appello> appelli = new ArrayList<Appello>();
		String query = "SELECT appello_id, appello_data FROM vw_appelli_corso WHERE corso_id = ? ORDER BY appello_data DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
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
	
}
