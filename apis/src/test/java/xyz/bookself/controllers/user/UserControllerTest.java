package xyz.bookself.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private final String apiPrefix = "/v1/users";
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookListRepository bookListRepository;

    @Test
    @WithBookselfUserDetails(id = 1)
    void testGetUser_OK()
            throws Exception {
        final User userExists = new User();

        userExists.setUsername("dummyUserName");
        userExists.setEmail(("dummy@dummy.com"));
        userExists.setPasswordHash(UUID.randomUUID().toString().replace("-", ""));

        when(userRepository.findById(1)).thenReturn(Optional.of(userExists));

        mockMvc.perform(get(apiPrefix + "/" + 1))
                .andExpect(status().isOk());
    }

    @Test
    void newUserInputIsGiven_soANewUserIsCreated() throws Exception {

        final User userExists = new User();
        final String newUserEmail = "newUser@newNew.com";
        final String newUserName = "newUser";
        userExists.setId(1);
        userExists.setUsername(newUserEmail);
        userExists.setEmail(newUserName);
        userExists.setPasswordHash("123");

        final UserDto userDto = new UserDto();
        userDto.setUsername(newUserEmail);
        userDto.setEmail(newUserName);
        userDto.setPassword("123");

        when(userRepository.save(userExists)).thenReturn(userExists);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(userDto);

        mockMvc.perform(post(apiPrefix + "/new-user").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void givenUserId_userListsShouldBeReturned() throws Exception {
        final Integer givenUserId = 99;
        final Collection<BookList> fourBookLists = IntStream.range(0, 4).mapToObj(i -> {
            BookList b = new BookList();
            b.setId(Integer.toHexString(i));
            b.setUserId(givenUserId);
            return b;
        }).collect(Collectors.toSet());

        when(bookListRepository.findUserBookLists(givenUserId)).thenReturn(fourBookLists);

        mockMvc.perform(get(apiPrefix + "/" + givenUserId + "/book-lists"))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_NotFound() throws Exception {
        Integer givenUserId = 1;
        when(userRepository.findById(givenUserId)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(get(apiPrefix + "/" + givenUserId))
            .andExpect(status().isNotFound());
    }
}


