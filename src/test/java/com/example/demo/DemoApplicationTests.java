package com.example.demo;

import com.example.demo.ulti.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String nowInOffsetFormat = DateTimeUtils.getNowInOffsetFormat(now);
        System.out.println(nowInOffsetFormat);
    }

    @Test
    void remainingDate() {
        String s = DateTimeUtils.remainingDate("2026-07-23T00:00:00",
                "2025-07-23T00:00:00",
                "2025-05-18T07:00:00.000");
        assertEquals("431/366", s);
    }
}
