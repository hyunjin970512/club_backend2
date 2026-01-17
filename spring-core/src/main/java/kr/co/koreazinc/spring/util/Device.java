package kr.co.koreazinc.spring.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.ObjectUtils;

public class Device {

    public static enum Type {

        /**
         * Represents a normal device. i.e. a browser on a desktop or laptop computer
         */
        NORMAL,
        /**
         * Represents a mobile device, such as an iPhone
         */
        MOBILE,
        /**
         * Represents a tablet device, such as an iPad
         */
        TABLET
    }

    public static enum Platform {

        /**
         * Represents an apple platform
         */
        IOS,

        /**
         * Represents an android platform
         */
        ANDROID,

        /**
         * Represents unknown platform
         */
        UNKNOWN
    }

    private final Type type;

    private final Platform platform;

    public Device(Type type, Platform platform) {
        this.type = type;
        this.platform = platform;
    }

    public Device(Type type) {
        this(type, Platform.UNKNOWN);
    }

    public Device() {
        this(Type.NORMAL, Platform.UNKNOWN);
    }

    public String toString() {
        return this.type.toString() + "-" + this.platform.toString();
    }

    public static Device fromString(String str) {
        List<String> token = Arrays.asList(str.toUpperCase().split("-"));
        if (ObjectUtils.isEmpty(token) || token.size() == 0) {
            return new Device();
        } else if(token.size() == 1) {
            return new Device(Type.valueOf(token.get(0)));
        } else {
            return new Device(Type.valueOf(token.get(0)), Platform.valueOf(token.get(1)));
        }
    }

    public Type getType() {
        return this.type;
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public boolean isNormal() {
        return this.type == Type.NORMAL;
    }

    public boolean isMobile() {
        return this.type == Type.MOBILE;
    }

    public boolean isTablet() {
        return this.type == Type.TABLET;
    }
}
