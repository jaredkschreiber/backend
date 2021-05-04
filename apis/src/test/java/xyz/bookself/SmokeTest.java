package xyz.bookself;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.controllers.health.HealthCheckController;
import xyz.bookself.controllers.book.BookController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SmokeTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookController bookController;

    @Autowired
    private HealthCheckController healthCheckController;

    @Test
    void bookRepositoryLoads() {
        assertThat(bookRepository).isNotNull();
    }

    @Test
    void bookControllerLoads() {
        assertThat(bookController).isNotNull();
    }

    @Test
    void healthCheckControllerLoads() {
        assertThat(healthCheckController).isNotNull();
    }
}
