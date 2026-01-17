package kr.co.koreazinc.tomcat.embedded;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.springframework.util.StringUtils;

public class CustomRequest extends Request {

    public CustomRequest(Connector connector) {
        super(connector);
    }

    @Override
    public String getQueryString() {
        String query = super.getQueryString();
        if (!StringUtils.hasText(query)) return null;
        return query
            .replaceAll("\\|", "%7C")
            .replaceAll(" ", "%20")
        ;
    }
}