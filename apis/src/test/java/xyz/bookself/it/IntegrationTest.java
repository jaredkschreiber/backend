package xyz.bookself.it;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegrationTest extends AbstractIT {

    @Test
    void test() {
        Assertions.assertNotNull(postgreDBContainer);
        Assertions.assertDoesNotThrow(() -> authorRepository.findAnyAuthors(1));
    }

}
