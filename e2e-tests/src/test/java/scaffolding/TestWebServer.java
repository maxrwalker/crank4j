package scaffolding;

import com.danielflower.crank4j.sharedstuff.Crank4jException;
import com.danielflower.crank4j.sharedstuff.Porter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;

public class TestWebServer implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TestWebServer.class);

    public final URI uri;
    private final Server jettyServer;

    public TestWebServer(int port) {
        this.uri = URI.create("http://localhost:" + port);
        jettyServer = new Server(new InetSocketAddress(uri.getHost(), uri.getPort()));
        jettyServer.setStopAtShutdown(true);
    }

    private void start() throws Exception {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler());
        jettyServer.setHandler(handlers);
        jettyServer.start();
        log.info("Started at " + uri);
    }

    private static Handler resourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource("/web", true, false));
        return resourceHandler;
    }

    public static TestWebServer startOnRandomPort() {
        int port = Porter.getAFreePort();
        TestWebServer server = new TestWebServer(port);
        try {
            server.start();
        } catch (Exception e) {
            throw new Crank4jException("Error starting test web server", e);
        }
        return server;
    }

    @Override
    public void close() throws Exception {
        jettyServer.stop();
    }
}
