package it.polimi.tiw.projects.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletContext;
import java.sql.DriverManager;
import it.polimi.tiw.projects.beans.Corso;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.StudenteDAO;
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

public class CorsoDelloStudenteChecker implements Filter {
	private Connection connection = null;

	/**
	 * Default constructor.
	 */
	public CorsoDelloStudenteChecker() {
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
		StudenteDAO sDAO = new StudenteDAO(connection, u.getId());
		List<Corso> corsi = null;
		try {
			corsi = sDAO.findCorsiOrdDecr();
		} catch (SQLException e) {
			// throw new ServletException(e);
			res.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in studente's corsi's database extraction");
			return;
		}

		String cID = request.getParameter("corsoid");
		try {
		if (cID != null) {
			int corsoID = Integer.parseInt(cID);
			boolean presente = false;

			for (Corso c : corsi) {
				if(c.getId() == corsoID) {
					presente = true;
					break;
				}
			}
			
			if (!presente) {
				res.sendRedirect(errorPage);
				return;
			}
		}
		} catch (NumberFormatException n) {
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Failure in parameter parsing");
			return;
		}
		
		// pass the request along the filter chain
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
