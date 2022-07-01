package de.malte.gql;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import io.smallrye.graphql.entry.http.ExecutionServlet;
import io.smallrye.graphql.entry.http.SchemaServlet;

public class Start {
    private static final int HTTP_PORT = 8080;

    public static void main(String[] args) throws Exception {

        Server jettyServer = createJettyserver();

        jettyServer.start();

        System.out.println("Server startet... press any key to exit");
        System.in.read();

        jettyServer.stop();

        System.out.println("Finished");
    }

    private static Server createJettyserver() throws IOException {
        Server jettyServer = new Server();

        //Create Jettycontext
        WebAppContext webappContext = new WebAppContext();
        webappContext.setContextPath("/");

        //Servlets
        ServletHolder servlet = new ServletHolder(ExecutionServlet.class);
        servlet.setInitOrder(1);

        ServletHolder schemaServlet = new ServletHolder(SchemaServlet.class);
        schemaServlet.setInitOrder(2);

        webappContext.addServlet(servlet, "/graphql/*");
        webappContext.addServlet(schemaServlet, "/graphql/schema.graphql");
        webappContext.setResourceBase("");

        //Enable Websocket
        WebSocketServerContainerInitializer.configure(webappContext, (servletContext, serverContainer) -> {/* nothing to configure*/});

        //Baseresouce with graphql-ui
        List<URL> hits = Collections.list(Start.class.getClassLoader().getResources("META-INF/resources"));
        Resource[] resourceArray = hits.stream()
                .map(url -> Resource.newResource(url))
                .toArray(Resource[]::new);

        Resource resourceColl = new ResourceCollection(resourceArray);
        webappContext.setBaseResource(resourceColl);


        //Add all to Server
        jettyServer.setHandler(webappContext);


        //Serverconnector http only
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);

        ServerConnector httpConnector = new ServerConnector(jettyServer, new HttpConnectionFactory(httpConfig));
        httpConnector.setPort(HTTP_PORT);

        jettyServer.setConnectors(new ServerConnector[] {httpConnector});
        return jettyServer;
    }

}
