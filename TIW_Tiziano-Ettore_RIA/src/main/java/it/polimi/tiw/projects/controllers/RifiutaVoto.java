package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Esito;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.StudenteDAO;

@WebServlet("/RifiutaVoto")
public class RifiutaVoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public RifiutaVoto() {
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

		String idAppello = request.getParameter("appelloid");
		int idApp;

		if (idAppello != null) {
			try {
				idApp = Integer.parseInt(idAppello);
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in voto's database insertion");
			return;
		}

		Esito esito = null;
		try {
			esito = sDAO.findEsitoAppello(idApp);
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
					"Failure in voto's database insertion (sql exception)");
			return;
		}
		String statoVal = esito.getStatoDiValutazione();
		String votoString = esito.getVoto();
		if (votoString == null) {
			response.sendRedirect(request.getServletContext().getContextPath() + "/Error.html");
			return;
		}
		int voto = -1;
		try {
			voto = Integer.parseInt(votoString);
		} catch (NumberFormatException e) {
			if (votoString.equals("30 e lode")) {
				voto = 30;
			} else {
				voto = -1;
			}
		}

		if (("pubblicato").equals(statoVal) && voto >= 18 && voto <= 30) {
			try {
				sDAO.rifiutaVoto(idApp);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in esito's database insertion");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Condizioni per rifiuto del voto non soddisfatte");
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