package xyz.bookself.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bookself.cors")
@Getter
@Setter
public class BookselfCorsConfiguration {
    private String allowedOrigins;
}
