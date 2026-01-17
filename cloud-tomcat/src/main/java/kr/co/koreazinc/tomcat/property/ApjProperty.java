package kr.co.koreazinc.tomcat.property;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;

import kr.co.koreazinc.tomcat.embedded.CustomConnector;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "server.ajp")
public class ApjProperty {

    private boolean enabled = false;

    private String protocol = "AJP/1.3";

    private String address = "0.0.0.0";

    private int port = 8009;

    private boolean secure = false;

    private boolean allowTrace = false;

    private String scheme = "http";

    private int redirectPort = 8443;

    private int maxPostSize = 20 * 1024 * 1024; // 20MB

    private int maxSavePostSize = 40 * 1024; // 40KB

    public Connector createConnector() {
        Connector ajpConnector = new CustomConnector(this.protocol);
        ajpConnector.setPort(this.port);
        ajpConnector.setProperty("address", this.address);
        ajpConnector.setSecure(this.secure);
        ajpConnector.setAllowTrace(this.allowTrace);
        ajpConnector.setScheme(this.scheme);
        ajpConnector.setRedirectPort(this.redirectPort);
        ajpConnector.setMaxPostSize(maxPostSize);
        ajpConnector.setMaxSavePostSize(maxSavePostSize);
        if (ajpConnector.getProtocolHandler() instanceof AbstractAjpProtocol channel) {
            channel.setSecretRequired(false);
            channel.setMinSpareThreads(10);
            channel.setConnectionTimeout(60000);
            channel.setAcceptCount(1000);
            channel.setKeepAliveTimeout(20000);
        }
        return ajpConnector;
    }
}