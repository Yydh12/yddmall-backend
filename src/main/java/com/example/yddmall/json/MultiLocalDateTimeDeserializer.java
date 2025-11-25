package com.example.yddmall.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 支持多种字符串/时间戳格式的 LocalDateTime 反序列化
 * 兼容：
 * - yyyy-MM-dd HH:mm:ss
 * - ISO_LOCAL_DATE_TIME（如 2025-11-06T16:00:00）
 * - ISO_INSTANT / 带Z或偏移量（如 2025-11-06T16:00:00.000Z 或 +08:00）
 * - 毫秒/秒时间戳
 */
public class MultiLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null) {
            // 处理数值型时间戳
            if (p.getCurrentToken() != null && p.getCurrentToken().isNumeric()) {
                long ts = p.getLongValue();
                // 猜测单位：> 10^11 视为毫秒，否则视为秒
                Instant instant = ts > 100_000_000_000L ? Instant.ofEpochMilli(ts) : Instant.ofEpochSecond(ts);
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            return null;
        }

        text = text.trim();
        if (text.isEmpty()) return null;

        // 1) yyyy-MM-dd HH:mm:ss
        try {
            return LocalDateTime.parse(text, FORMATTER_SPACE);
        } catch (Exception ignored) {}

        // 2) ISO_LOCAL_DATE_TIME（不含时区偏移）
        try {
            return LocalDateTime.parse(text);
        } catch (Exception ignored) {}

        // 3) ISO_INSTANT（含 Z 或偏移量），转换到本地时区
        try {
            Instant instant = Instant.parse(text);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception ignored) {}

        // 4) 再尝试毫秒/秒时间戳（字符串）
        try {
            long ts = Long.parseLong(text);
            Instant instant = ts > 100_000_000_000L ? Instant.ofEpochMilli(ts) : Instant.ofEpochSecond(ts);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception ignored) {}

        // 无法解析则返回 null 或抛出异常，视业务选择
        return null;
    }
}