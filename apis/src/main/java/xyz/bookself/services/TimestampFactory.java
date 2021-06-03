package xyz.bookself.services;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * To make LocalDateTime.now() testable with mockito...
 */
@Component
public class TimestampFactory {
    public LocalDateTime getTimestamp() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
