package xyz.bookself.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookselfUserDetailsServiceTest {

    public static final String A_USER = "a-user";

    @Mock
    private UserRepository userRepository;
    private BookselfUserDetailsService service;

    @BeforeEach
    void beforeEach() {
        service = new BookselfUserDetailsService(userRepository);
    }

    @Test
    void testUserFound() {
        when(userRepository.findUserByUsername(A_USER)).thenReturn(Optional.of(new User()));
        assertDoesNotThrow(() -> service.loadUserByUsername(A_USER));
    }

    @Test
    void testUserNotFound() {
        when(userRepository.findUserByUsername(A_USER)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(A_USER));
    }

}