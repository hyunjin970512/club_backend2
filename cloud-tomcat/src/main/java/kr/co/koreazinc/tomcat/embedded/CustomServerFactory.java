package kr.co.koreazinc.tomcat.embedded;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.Engine;
import org.apache.catalina.Executor;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.modeler.Registry;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.TomcatHttpHandlerAdapter;

public class CustomServerFactory {

    public static class Servlet extends TomcatServletWebServerFactory {

        public Servlet() {
            super();
        }
    }

    public static class Reactive extends TomcatReactiveWebServerFactory {

        public Reactive() {
            super();
        }

        private File baseDirectory;

        private int backgroundProcessorDelay;

        private boolean disableMBeanRegistry = true;

        @Override
        public WebServer getWebServer(HttpHandler httpHandler) {
            if (this.disableMBeanRegistry) {
                Registry.disableRegistry();
            }
            Tomcat tomcat = new Tomcat();
            File baseDir = (this.baseDirectory != null) ? this.baseDirectory : createTempDir("tomcat");
            tomcat.setBaseDir(baseDir.getAbsolutePath());
            for (LifecycleListener listener : getDefaultServerLifecycleListeners()) {
                tomcat.getServer().addLifecycleListener(listener);
            }
            Connector connector = new CustomConnector(TomcatReactiveWebServerFactory.DEFAULT_PROTOCOL);
            connector.setThrowOnFailure(true);
            tomcat.getService().addConnector(connector);
            customizeConnector(connector);
            tomcat.setConnector(connector);
            registerConnectorExecutor(tomcat, connector);
            tomcat.getHost().setAutoDeploy(false);
            configureEngine(tomcat.getEngine());
            for (Connector additionalConnector : getAdditionalTomcatConnectors()) {
                tomcat.getService().addConnector(additionalConnector);
                registerConnectorExecutor(tomcat, additionalConnector);
            }
            TomcatHttpHandlerAdapter servlet = new TomcatHttpHandlerAdapter(httpHandler);
            prepareContext(tomcat.getHost(), servlet);
            return getTomcatWebServer(tomcat);
        }

        private List<LifecycleListener> getDefaultServerLifecycleListeners() {
            AprLifecycleListener aprLifecycleListener = new AprLifecycleListener();
            return AprLifecycleListener.isAprAvailable() ? new ArrayList<>(Arrays.asList(aprLifecycleListener)) : new ArrayList<>();
        }

        private void registerConnectorExecutor(Tomcat tomcat, Connector connector) {
            if (connector.getProtocolHandler().getExecutor() instanceof Executor executor) {
                tomcat.getService().addExecutor(executor);
            }
        }

        private void configureEngine(Engine engine) {
            engine.setBackgroundProcessorDelay(this.backgroundProcessorDelay);
            for (Valve valve : getEngineValves()) {
                engine.getPipeline().addValve(valve);
            }
        }

        @Override
        public void setBackgroundProcessorDelay(int delay) {
            this.backgroundProcessorDelay = delay;
        }

        @Override
        public void setBaseDirectory(File baseDirectory) {
            this.baseDirectory = baseDirectory;
            super.setBaseDirectory(baseDirectory);
        }

        @Override
        public void setDisableMBeanRegistry(boolean disableMBeanRegistry) {
            this.disableMBeanRegistry = disableMBeanRegistry;
            super.setDisableMBeanRegistry(disableMBeanRegistry);
        }
    }
}