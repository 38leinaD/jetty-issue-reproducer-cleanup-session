package acme;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet(urlPatterns = "/hello")
public class AServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	ABean bean;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		bean.setValue("123");

		resp.setContentType("text/html");
		PrintWriter printWriter = resp.getWriter();
		printWriter.print("<html>");
		printWriter.print("<body>");
		printWriter.print("<h1>Hello World HttpServlet Class Example</h1>");
		printWriter.print("</body>");
		printWriter.print("</html>");
		printWriter.close();
	}
}
