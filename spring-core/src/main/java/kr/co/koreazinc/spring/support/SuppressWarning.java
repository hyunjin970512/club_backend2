package kr.co.koreazinc.spring.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuppressWarning {

    // 모든 경고
    public static final String ALL = "all";

    // 캐스트 연산자 관련 경고
    public static final String CAST = "cast";

    // 사용하지 말아야 할 주석 관련 경고
    public static final String DEP_ANN = "dep-ann";

    // 사용하지 말아야 할 메서드 관련 경고
    public static final String DEPRECATION = "deprecation";

    // switch문에서 break 누락 관련 경고
    public static final String FALLTHROUGH = "fallthrough";

    // 반환하지 않는 finally 블럭 관련 경고
    public static final String FINALLY = "finally";

    // null 분석 관련 경고
    public static final String NULL = "null";

    // 제너릭을 사용하는 클래스 매개 변수가 불특정일 때의 경고
    public static final String RAWTYPES = "rawtypes";

    // 검증되지 않은 연산자 관련 경고
    public static final String UNCHECKED = "unchecked";

    // 사용하지 않는 코드 관련 경고
    public static final String UNUSED = "unused";
}