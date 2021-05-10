package xyz.bookself.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bookself.api")
@Getter
@Setter
public class BookselfApiConfiguration {
    private int maxReturnedBooks;
    private int maxReturnedGenres;
}
