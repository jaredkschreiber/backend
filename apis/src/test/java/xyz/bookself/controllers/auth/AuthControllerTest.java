package xyz.bookself.controllers.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.domain.PasswordResetToken;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.PasswordResetTokenRepository;
import xyz.bookself.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.bookself.controllers.user.UserControllerTest.APPLICATION_JSON_UTF8;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private final String apiPrefix = "/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordResetTokenRepository tokenRepository;

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

    @Test
    void testForgotPassword_NotFound() throws Exception {
        final ForgotPasswordDto forgotPasswordDto = new ForgotPasswordDto();
        forgotPasswordDto.setEmail("dummy@dummy.com");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(forgotPasswordDto);

        mockMvc.perform(post(apiPrefix + "/forgot-password").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testResetPassword_NotFound() throws Exception {
        final ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setPassword("123456");
        resetPasswordDto.setToken("123456");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(resetPasswordDto);

        mockMvc.perform(post(apiPrefix + "/reset-password").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testResetPassword_Forbidden() throws Exception {
        final User userExists = new User();
        userExists.setId(1);
        userExists.setUsername("dummyUserName");
        userExists.setEmail("dummy@dummy.com");
        userExists.setPasswordHash(UUID.randomUUID().toString().replace("-", ""));

        when(userRepository.findById(1)).thenReturn(Optional.of(userExists));

        final PasswordResetToken tokenExists = new PasswordResetToken();
        tokenExists.setId(1);
        tokenExists.setUser(userExists);
        tokenExists.setToken("123456");
        tokenExists.setExpiration(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(tokenExists));

        final ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setPassword("123456");
        resetPasswordDto.setToken("123456");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(resetPasswordDto);

        mockMvc.perform(post(apiPrefix + "/reset-password").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testResetPassword_OK() throws Exception {
        final User userExists = new User();
        userExists.setId(1);
        userExists.setUsername("dummyUserName");
        userExists.setEmail("dummy@dummy.com");
        userExists.setPasswordHash(UUID.randomUUID().toString().replace("-", ""));

        when(userRepository.findById(1)).thenReturn(Optional.of(userExists));

        final PasswordResetToken tokenExists = new PasswordResetToken();
        tokenExists.setId(1);
        tokenExists.setUser(userExists);
        tokenExists.setToken("123456");
        tokenExists.setExpiration(LocalDateTime.now().plusMinutes(15));

        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(tokenExists));

        final ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setPassword("123456");
        resetPasswordDto.setToken("123456");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(resetPasswordDto);

        mockMvc.perform(post(apiPrefix + "/reset-password").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk());
    }
}


