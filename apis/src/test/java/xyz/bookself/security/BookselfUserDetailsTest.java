package xyz.bookself.security;

import org.junit.jupiter.api.Test;
import xyz.bookself.users.domain.User;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A bunch of stupid tests
 */
class BookselfUserDetailsTest {

    @Test
    void test() {
        var user = new User();
        user.setUsername("a-user");
        user.setPasswordHash("a-pass");
        var userDetails = new BookselfUserDetails(user);
        assertEquals("a-user", userDetails.getUsername());
        assertEquals("a-pass", userDetails.getPassword());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

}