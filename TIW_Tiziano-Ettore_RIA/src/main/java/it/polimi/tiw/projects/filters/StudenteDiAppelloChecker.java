package it.polimi.tiw.projects.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletContext;
import java.sql.DriverManager;

import it.polimi.tiw.projects.dao.AppelloDAO;
import it.polimi.tiw.projects.beans.Verbale;
import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.StudenteAndEsitoAppello;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.CorsoDAO;
import it.polimi.tiw.projects.dao.DocenteDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet Filter implementation class checker
 */

public class StudenteDiAppelloChecker implements Filter {
	private Connection connection = null;

	/**
	 * Default constructor.
	 */
	public StudenteDiAppelloChecker() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String errorPage = req.getServletContext().getContextPath() + "/Error.html";

		HttpSession s = req.getSession();
		User u = (User) s.getAttribute("user");
		
		boolean trovato = false;
		
		String appID = request.getParameter("appelloid");
		try {
		if (appID != null) {
			int appelloID = Integer.parseInt(appID);
			
			AppelloDAO aDAO = new AppelloDAO(connection, appelloID);
			try {
			List<StudenteAndEsitoAppello> listaStudenti = aDAO.findDatiAppello();
			
			
			String studID = request.getParameter("studenteid");
			if (studID != null) {
				int studenteID = Integer.parseInt(studID);
				
				for (StudenteAndEsitoAppello stud : listaStudenti) {
					if (stud.getId() == studenteID) {
						if (stud.getStatoValutazione().equals("inserito") || stud.getStatoValutazione().equals("non inserito")) {
							trovato = true;
							break;
						}
					}
				}
			}
			
			
			
			} catch (SQLException e) {
				// throw new ServletException(e);
				res.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in appello's database extraction");
				return;
			}
		}
		} catch (NumberFormatException n) {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
			return;
		}
		
		
		if (!trovato) {
			res.sendRedirect(errorPage);
			return;
		}
		
		
		
		chain.doFilter(request, response);
	}


	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		try {
			ServletContext context = fConfig.getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			this.connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			throw new ServletException("Can't initialize DB connection in filter");
		}
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
