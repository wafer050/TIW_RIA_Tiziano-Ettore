package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.Esito;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.DocenteDAO;
import it.polimi.tiw.projects.dao.StudenteDAO;
import it.polimi.tiw.projects.dao.CorsoDAO;

@WebServlet("/GetEsitoStudente")
public class GetEsitoStudente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetEsitoStudente() {
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

		HttpSession s = request.getSession();
		User u = (User) s.getAttribute("user");
		StudenteDAO sDAO = new StudenteDAO(connection, u.getId());
		String appelloID = request.getParameter("appelloid");
		Esito esito = null;
		// sanitising request parameter
		try {
			if (appelloID != null) {
				int aID = Integer.parseInt(appelloID);
				try {
					esito = sDAO.findEsitoAppello(aID);
				} catch (SQLException e) {
					// throw new ServletException(e);
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
							"Failure in studente's appelli's database extraction");
					return;
				}
			}
		} catch (NumberFormatException n) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
			return;
		}

		String json = new Gson().toJson(esito);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

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