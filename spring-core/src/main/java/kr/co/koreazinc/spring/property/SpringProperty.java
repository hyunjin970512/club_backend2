package kr.co.koreazinc.spring.property;

import kr.co.koreazinc.spring.utility.PropertyUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringProperty {

    public static final String PROFILES_ACTIVE = PropertyUtils.getProperty("spring.profiles.active", "local");

    public static final boolean IS_LOCAL = "local".equalsIgnoreCase(PROFILES_ACTIVE);
}