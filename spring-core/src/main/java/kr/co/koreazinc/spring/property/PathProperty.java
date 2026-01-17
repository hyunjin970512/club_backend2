package kr.co.koreazinc.spring.property;

import kr.co.koreazinc.spring.utility.PropertyUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathProperty {

    public static final String FAVICON = "/favicon.ico";

    public static final String ROBOTS = "/robots.txt";

    public static final String RESOURCES = "/resources";

    public static final String RESOURCES_COMPONENT = RESOURCES + "/component";

    public static final String RESOURCES_MODULE = RESOURCES + "/module";

    public static final String ERROR = PropertyUtils.getProperty("server.error.path", "/error");

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class API {

        public static final String V1 = "/v1";

        public static final String V2 = "/v2";

        public static final String V3 = "/v3";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class POPUP {

        public static final String BASE = PropertyUtils.getProperty("server.popup.path", "/popup");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class INNER {

        public static final String BASE = PropertyUtils.getProperty("server.inner.path", "/inner");
    }
}