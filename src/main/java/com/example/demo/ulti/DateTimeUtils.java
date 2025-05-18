package com.example.demo.ulti;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static String getNowInOffsetFormat(LocalDateTime localDateTime) {
        try {
            OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.ofHours(7));
            return offsetDateTime.format(FORMATTER);
        }catch (Exception e) {
            return null;
        }
    }

    public static String remainingDate(String policyEndDate, String policyStartDate, String terminationDate) {
        try {
            OffsetDateTime end = parseDate(policyEndDate);
            OffsetDateTime start = parseDate(policyStartDate);
            OffsetDateTime termination = parseDate(terminationDate);

            long denominator = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()) + 1;
            long numerator = ChronoUnit.DAYS.between(termination.toLocalDate(), end.toLocalDate());

            return numerator + "/" + denominator;
        } catch (Exception e) {
            return null;
        }
    }

    private static OffsetDateTime parseDate(String dateStr) {
        // Thêm xử lý nếu chuỗi không có offset thì tự gán +07:00
        if (!dateStr.endsWith("Z") && !dateStr.contains("+")) {
            dateStr += "+07:00";
        }
        return OffsetDateTime.parse(dateStr);
    }
}
