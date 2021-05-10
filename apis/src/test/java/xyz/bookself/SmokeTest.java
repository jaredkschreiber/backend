package xyz.bookself;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.bookself.books.repository.AuthorRepository;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.controllers.book.AuthorController;
import xyz.bookself.controllers.book.GenreController;
import xyz.bookself.controllers.health.HealthCheckController;
import xyz.bookself.controllers.book.BookController;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SmokeTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookController bookController;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorController authorController;

    @Autowired
    private GenreController genreController;

    @Autowired
    private HealthCheckController healthCheckController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookListRepository bookListRepository;

    @Test
    void bookRepositoryLoads() {
        assertThat(bookRepository).isNotNull();
    }

    @Test
    void bookControllerLoads() {
        assertThat(bookController).isNotNull();
    }

    @Test
    void authorRepositoryLoads() {
        assertThat(authorRepository).isNotNull();
    }

    @Test
    void setAuthorControllerLoads() {
        assertThat(authorController).isNotNull();
    }

    @Test
    void genreControllerLoads() {
        assertThat(genreController).isNotNull();
    }

    @Test
    void healthCheckControllerLoads() {
        assertThat(healthCheckController).isNotNull();
    }

    @Test
    void userRepositoryLoads() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    void bookListRepositoryLoads() {
        assertThat(bookListRepository).isNotNull();
    }
}
