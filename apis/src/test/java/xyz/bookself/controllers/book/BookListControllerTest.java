package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.Rating;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class BookListControllerTest {
    private static final String apiPrefix = "/v1/book-lists";

    private static final int authenticatedUserId = 1;
    private static final String bookListId = "1234";
    private static final String bookId = "4321";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookListRepository bookListRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void addBookToBookList_Success() throws Exception {
        var bookList = new BookList();
        bookList.setUserId(authenticatedUserId);

        var book = new Book();
        book.setId(bookId);

        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(bookList));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(post(apiPrefix + "/" + bookListId + "/books/" + bookId))
            .andExpect(status().isCreated());
    }

    @Test
    @WithBookselfUserDetails(id = 2)
    void addBookToBookList_Unauthorized() throws Exception {
        var bookList = new BookList();
        bookList.setUserId(authenticatedUserId);

        var book = new Book();
        book.setId(bookId);

        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(bookList));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(post(apiPrefix + "/" + bookListId + "/books/" + bookId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void addBookToBookList_BookAlreadyInList_BadRequest() throws Exception {
        var bookList = new BookList();
        bookList.setUserId(authenticatedUserId);

        var book = new Book();
        book.setId(bookId);

        Set<String> books = new HashSet<>();
        books.add(book.getId());
        bookList.setBooks(books);

        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(bookList));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(post(apiPrefix + "/" + bookListId + "/books/" + bookId))
            .andExpect(status().isBadRequest());
    }
}
