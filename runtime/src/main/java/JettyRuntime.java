import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.cdi.CdiDecoratingListener;
import org.eclipse.jetty.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.DecoratingListener;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.weld.environment.servlet.EnhancedListener;
import org.jboss.weld.environment.servlet.WeldServletLifecycle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import java.util.concurrent.CountDownLatch;

@ApplicationScoped
public class JettyRuntime {

	private Server server;

	private CountDownLatch shutdownLatch = new CountDownLatch(1);

	public void startJetty() throws Exception {

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("Shutdown hook called!");
				shutdownLatch.countDown();
			}));

	        server = new Server();
	        
	        // jetty-annotations
	        // https://www.eclipse.org/jetty/documentation/current/using-annotations-embedded.html
	        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
			classlist.addBefore(WebInfConfiguration.class.getName(), AnnotationConfiguration.class.getName());
			classlist.replace(WebInfConfiguration.class.getName(), EmbeddedWebInfConfiguration.class.getName());
			classlist.addAfter(JettyWebXmlConfiguration.class.getName(), EnvConfiguration.class.getName());
			classlist.addAfter(EnvConfiguration.class.getName(), PlusConfiguration.class.getName());
	
	        ServerConnector connector = new ServerConnector(server);
	        connector.setPort(8080);
	        server.setConnectors(new Connector[] { connector });
	
	        WebAppContext context = new WebAppContext();
			context.setClassLoader(JettyRuntime.class.getClassLoader());

	        context.setAttribute(WeldServletLifecycle.BEAN_MANAGER_ATTRIBUTE_NAME, CDI.current().getBeanManager());

	        // jetty-webapp-decorate
	        context.addEventListener(new DecoratingListener(ServletContextHandler.getServletContextHandler(context.getServletContext()), DecoratingListener.DECORATOR_ATTRIBUTE));
	        context.setInitParameter(
	        		CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE,
	                CdiDecoratingListener.MODE);
	        context.addBean(new ServletContextHandler.Initializer(context,
	             new EnhancedListener()));
	            context.addBean(new ServletContextHandler.Initializer(context,
	                new CdiServletContainerInitializer()));
	
	        context.setServer(server);
	        server.insertHandler(context);
	        
		    context.setThrowUnavailableOnStartupException(true);
			
	        server.start();

			context.getSessionHandler().getSessionIdManager().getSessionHouseKeeper().setIntervalSec(10);

			System.out.println("Jetty started.");

			shutdownLatch.await();
	}
}
