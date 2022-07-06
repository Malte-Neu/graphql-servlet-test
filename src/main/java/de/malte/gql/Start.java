package de.malte.gql;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.cdi.CdiDecoratingListener;
import org.eclipse.jetty.cdi.CdiServletContainerInitializer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

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
        //Version 1
        //https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html#_weld_se_and_weld_servlet_cooperation
//        Weld weld = new Weld();
//        WeldContainer container = weld.initialize();
        //see also below the line webappContext.setAttribute(WeldServletLifecycle.BEAN_MANAGER_ATTRIBUTE_NAME


        Server jettyServer = new Server();

        //Create Jettycontext
        ServletContextHandler webappContext = new WebAppContext();//new ServletContextHandler();
        webappContext.setContextPath("/");


        //Servlets
//        ServletHolder servlet = new ServletHolder(ExecutionServlet.class);
//        servlet.setInitOrder(1);
//
//        ServletHolder schemaServlet = new ServletHolder(SchemaServlet.class);
//        schemaServlet.setInitOrder(2);
//
//        webappContext.addServlet(servlet, "/graphql/*");
//        webappContext.addServlet(schemaServlet, "/graphql/schema.graphql");

        webappContext.addServlet(ExecutionServlet.class, "/graphql/*");
        webappContext.addServlet(SchemaServlet.class, "/graphql/schema.graphql");

        //webappContext.setResourceBase("");

        //Version 1
        //https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html#_weld_se_and_weld_servlet_cooperation
//        webappContext.setAttribute(WeldServletLifecycle.BEAN_MANAGER_ATTRIBUTE_NAME, container.getBeanManager());


        //Baseresouce with graphql-ui
        List<URL> hits = Collections.list(Start.class.getClassLoader().getResources("META-INF/resources"));
        Resource[] resourceArray = hits.stream()
                .map(url -> Resource.newResource(url))
                .toArray(Resource[]::new);

        Resource resourceColl = new ResourceCollection(resourceArray);
        webappContext.setBaseResource(resourceColl);



        //Enable Websocket not working with version 3
//        JavaxWebSocketServletContainerInitializer.configure(webappContext, (servletContext, wsContainer) ->
//            wsContainer.addEndpoint(ExecutionServlet.class));


        //Version 3
        //https://github.com/jetty-project/embedded-jetty-weld/blob/jetty-10.0.x/src/main/java/org/eclipse/jetty/demos/ServerMain.java
        webappContext.setInitParameter(CdiServletContainerInitializer.CDI_INTEGRATION_ATTRIBUTE, CdiDecoratingListener.MODE);
        webappContext.addServletContainerInitializer(new CdiServletContainerInitializer());
        webappContext.addServletContainerInitializer(new org.jboss.weld.environment.servlet.EnhancedListener());

        //Add all to Server
        jettyServer.setHandler(webappContext);


        //Version 2
        //https://docs.jboss.org/weld/reference/latest/en-US/html/environments.html#_embedded_jetty
        //activate Weld CDI
        //webappContext.addEventListener(new org.eclipse.jetty.webapp.DecoratingListener()); throws a NPE
//        webappContext.addEventListener(new org.eclipse.jetty.webapp.DecoratingListener(webappContext));
//        webappContext.addEventListener(new org.jboss.weld.environment.servlet.Listener());





        //Serverconnector http only
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);

        ServerConnector httpConnector = new ServerConnector(jettyServer, new HttpConnectionFactory(httpConfig));
        httpConnector.setPort(HTTP_PORT);

        jettyServer.setConnectors(new ServerConnector[] {httpConnector});





        return jettyServer;
    }
}
