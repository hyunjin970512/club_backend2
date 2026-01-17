package kr.co.koreazinc.spring.util.time;

import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeFormat {

    public static final String ISO_OFFSET_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final DateTimeFormatter ISO_OFFSET_DATE_TIME = DateTimeFormatter.ofPattern(DateTimeFormat.ISO_OFFSET_DATE_TIME_PATTERN);
}
