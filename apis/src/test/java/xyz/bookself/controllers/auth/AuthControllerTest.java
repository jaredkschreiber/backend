package xyz.bookself.controllers.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private final String apiPrefix = "/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testSignIn_Unauthorized() throws Exception {
        mockMvc.perform(post(apiPrefix + "/signin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithBookselfUserDetails(id = 1)
    void testSignIn_OK()
            throws Exception {
        final User userExists = new User();

        userExists.setUsername("dummyUserName");
        userExists.setEmail(("dummy@dummy.com"));
        userExists.setPasswordHash(UUID.randomUUID().toString().replace("-", ""));

        when(userRepository.findById(1)).thenReturn(Optional.of(userExists));

        mockMvc.perform(post(apiPrefix + "/signin"))
                .andExpect(status().isOk());
    }
}


