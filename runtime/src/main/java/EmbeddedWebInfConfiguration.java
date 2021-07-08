import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.eclipse.jetty.util.resource.Resource.newResource;

public class EmbeddedWebInfConfiguration extends WebInfConfiguration {

	@Override
	protected List<Resource> findWebInfLibJars(WebAppContext context) throws Exception {

		Set<Resource> webInfLibJars = new HashSet<>();

		Enumeration<URL> urls = context.getClassLoader().getResources("META-INF/resources");
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			webInfLibJars.add(classpathUrlToResource(url));
		}
		return webInfLibJars.stream().collect(toList());
	}

	@Override
	public void unpack(WebAppContext context) throws IOException {

		URL url = context.getClassLoader().getResource("WEB-INF");

		if (url == null) {
			return;
		}
		context.setBaseResource(newResource(url.toString().split("!")[0] + "!/"));
	}
	
	private Resource classpathUrlToResource(URL url) throws IOException {
		if (url.toString().startsWith("file:")) {
		    // Either starting from Eclipse or finding a jar that is exploded into a directory
            Path resourcePath = Paths.get(url.getPath());
            String basePath = resourcePath.getParent().getParent().toString() + "/";
            return Resource.newResource(basePath);
		}
		else if (url.toString().startsWith("jar:"))  {
			return Resource.newResource(url.getPath().split("!")[0]);
		}
		else {
			throw new RuntimeException("Unexpected resource: " + url + ". Supporting file: and jar:");
		}
	}
}
