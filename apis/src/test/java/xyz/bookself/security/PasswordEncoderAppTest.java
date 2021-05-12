package xyz.bookself.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderAppTest {

    @Test
    void testRunWithoutArguments() {
        var hash = PasswordEncoderApp.run(new String[]{});
        assertNull(hash);
    }

    @Test
    void testRunWithArguments() {
        var hash = PasswordEncoderApp.run(new String[]{"Password1234!"});
        assertNotNull(hash);
    }

}