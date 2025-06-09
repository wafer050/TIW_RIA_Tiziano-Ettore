package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.StudenteAndEsitoAppello;
import it.polimi.tiw.projects.beans.Appello;
import it.polimi.tiw.projects.beans.StudenteDati;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.DocenteDAO;
import it.polimi.tiw.projects.dao.AppelloDAO;
import it.polimi.tiw.projects.dao.CorsoDAO;
import it.polimi.tiw.projects.dao.StudenteDAO;

@WebServlet("/GetDatiStudente")
public class GetDatiStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetDatiStudente() {
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
		StudenteDati datiStudente = null;
		String studenteID = request.getParameter("studenteid");
		try {
		if (studenteID != null) {
			int studID = Integer.parseInt(studenteID);
			StudenteDAO studDAO = new StudenteDAO(connection, studID);
			try {
				datiStudente = studDAO.findDatiStudente();
			} catch (SQLException e) {
				// throw new ServletException(e);
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
						"Failure in studente's database extraction");
				return;
			}
		}
		} catch (NumberFormatException n) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
			return;
		}
		
		String json = new Gson().toJson(datiStudente);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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