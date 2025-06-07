package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AppelloDAO;
import it.polimi.tiw.projects.dao.CorsoDAO;

@WebServlet("/PubblicaVoti")
public class PubblicaVoti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public PubblicaVoti() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//String idStudente = request.getParameter("idStudente");
		String idAppello = request.getParameter("appelloid");
		//int idStud;
		int idApp;
		
		//String param = request.getParameter("appelloid");
		//System.out.println("Valore ricevuto di appelloid: " + param);
		
		if (idAppello != null ) {
			//idStud = Integer.parseInt(idStudente);
			try {
			idApp = Integer.parseInt(idAppello);
			} catch (NumberFormatException n) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
					"1)Failure in stato_di_valutazione's database insertion");
			return;
		}
		
		AppelloDAO appDAO = new AppelloDAO(connection, idApp);
		try {
			appDAO.pubblica();
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "2)Failure in stato_di_valutazione's database insertion (sql exception)");
			return;
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response); // Handle POST as GET
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}