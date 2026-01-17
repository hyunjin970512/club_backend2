package kr.co.koreazinc.spring.http.utility;

import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {

    /**
     * Returns the base URL
     * For example: <strong>http://www.example.com:80</strong>/a/b?c=d#e
     * @return a <code>String</code> representing the base URL
     */
    public static String toBaseURI(URI uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        if (port == -1) return scheme + "://" + host;
        return scheme + "://" + host + ":" + port;
    }

    public static String getPath(URI uri, int index) {
        String[] path = uri.getPath().substring(1).split("/");
        return path[(path.length + index) % path.length];
    }

    public static Mutate mutate(URI uri) {
        return new Mutate(uri);
    }

    public static class Mutate {

        private String scheme;

        private String host;

        private int port;

        private String path;

        private String query;

        private String fragment;

        public Mutate(URI uri) {
            this.scheme = uri.getScheme();
            this.host = uri.getHost();
            this.port = uri.getPort();
            this.path = uri.getRawPath();
            this.query = uri.getRawQuery();
            this.fragment = uri.getRawFragment();
        }

        public Mutate scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Mutate host(String host) {
            this.host = host;
            return this;
        }

        public Mutate port(int port) {
            this.port = port;
            return this;
        }

        public Mutate path(String path) {
            this.path = path;
            return this;
        }

        public Mutate query(String query) {
            this.query = query;
            return this;
        }

        public Mutate fragment(String fragment) {
            this.fragment = fragment;
            return this;
        }

        public URI build() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.scheme).append("://");
            builder.append(this.host);
            if (this.port != -1) {
                builder.append(":").append(this.port);
            }
            if (this.path != null) {
                builder.append(this.path);
            }
            if (this.query != null) {
                builder.append("?").append(this.query);
            }
            if (this.fragment != null) {
                builder.append("#").append(this.fragment);
            }
            try {
                return new URI(builder.toString());
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }
}
