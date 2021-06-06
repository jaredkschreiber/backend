package xyz.bookself.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootTest
public class BookselfBasicAuthConfigurationTest {

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private Authentication authentication;

    @Test
    void logoutDoesNotThrowExceptions() {
        final BookselfLogoutHandler logoutHandler = new BookselfLogoutHandler();
        logoutHandler.logout(request, response, authentication);
    }
}
