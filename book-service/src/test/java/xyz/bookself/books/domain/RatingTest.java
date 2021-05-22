package xyz.bookself.books.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new Rating(null, null, null, null));
    }

}