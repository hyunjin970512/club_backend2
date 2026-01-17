package kr.co.koreazinc.tomcat.embedded;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;

public class CustomConnector extends Connector {

    public CustomConnector() {
        super();
    }

    public CustomConnector(String protocol) {
        super(protocol);
    }

    @Override
    public Request createRequest() {
        return new CustomRequest(this);
    }
}