package ru.practicum.service.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime[] decoder(String encodeStart, String encodeEnd) {
        String decodedStart = URLDecoder.decode(encodeStart, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(encodeEnd, StandardCharsets.UTF_8);

        LocalDateTime start = LocalDateTime.parse(decodedStart, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(decodedEnd, FORMATTER);

        return new LocalDateTime[]{start, end};
    }

    public static String[] encoder(String decodeStart, String decodeEnd) {
        String encodedStart = URLEncoder.encode(decodeStart, StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(decodeEnd, StandardCharsets.UTF_8);

        return new String[]{encodedStart, encodedEnd};
    }

    public static LocalDateTime toTime(String text) {
        return LocalDateTime.parse(text, FORMATTER);
    }
}