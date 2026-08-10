package com.car.rental.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局配置：统一 LocalDateTime/LocalDate/LocalTime 的序列化与反序列化格式。
 *
 * 解决前端传 "2026-07-09 09:00:00" 反序列化报错：
 * "Text '2026-07-09 09:00:00' could not be parsed at index 10"
 *
 * 根因：Spring Boot 自动注册 JavaTimeModule 后，LocalDateTime 默认用 ISO 格式
 * （yyyy-MM-dd'T'HH:mm:ss），application.yml 的 spring.jackson.date-format
 * 只对老 Date 类型生效，对 JSR-310 类型无效。这里通过 customizer 覆盖。
 */
@Configuration
public class JacksonConfig {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN);
            DateTimeFormatter tf = DateTimeFormatter.ofPattern(TIME_PATTERN);

            // 反序列化（前端 → 后端）
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(df));
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(tf));

            // 序列化（后端 → 前端）
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(df));
            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(tf));

            builder.modulesToInstall(javaTimeModule);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
