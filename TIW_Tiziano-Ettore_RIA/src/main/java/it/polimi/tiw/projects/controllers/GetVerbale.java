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

import it.polimi.tiw.projects.beans.Verbale;
import it.polimi.tiw.projects.dao.VerbaleDAO;


@WebServlet("/GetVerbale")
public class GetVerbale extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetVerbale() {
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
		//String idAppello = request.getParameter("appelloid");
		String verbaleId = request.getParameter("verbaleid");
		int verbId;
		
		if (verbaleId != null) {
			try {
			verbId = Integer.parseInt(verbaleId);
			} catch (NumberFormatException n) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
					"1)Failure in verbale's database retrieval");
			return;
		}
		
		
		
		
		
		
			VerbaleDAO vDAO = new VerbaleDAO(connection, verbId);
			Verbale verbale = null;
			try {
				verbale = vDAO.getDatiVerbale();
			} catch (SQLException e) {
				// throw new ServletException(e);
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "3)Failure in verbale's database retrieval (sql exception)");
				return;
			}
			
			
			try {
				verbale.setStudenti(vDAO.studentiVerbalizzati());
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "4)Failure in verbale's studenti's database retrieval (sql exception)");
				return;
			}
			
			String json = new Gson().toJson(verbale);
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