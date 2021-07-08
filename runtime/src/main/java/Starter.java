import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;

public class Starter {
    public static void start() {
        Weld weld = new Weld();
        try (WeldContainer container = weld.initialize()) {
            container.select(JettyRuntime.class).get().startJetty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        start();
    }

}