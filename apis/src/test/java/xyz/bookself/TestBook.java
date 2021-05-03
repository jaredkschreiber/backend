package xyz.bookself;

import xyz.bookself.books.domain.Book;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class TestBook {

    private final Book book = new Book();

    @Test
    void testBookCanBeInitialized() {
        assertThat(book, is(notNullValue()));
    }
}
