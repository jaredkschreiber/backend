package xyz.bookself.controllers.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookListRepository bookListRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private UserRepository userRepository;
    @Value("${bookself.api.max-returned-books}")
    private int maxReturnedBooks;

    @Test
    void getBookList_Success() throws Exception {
        var bookList = new BookList();
        bookList.setId(bookListId);

        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(bookList));
        mockMvc.perform(get(apiPrefix + "/" + bookListId))
            .andExpect(status().isOk());
    }

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

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void deleteBookFromBookList_Success() throws Exception {
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

        mockMvc.perform(delete(apiPrefix + "/" + bookListId + "/books/" + bookId))
            .andExpect(status().isOk());
    }

    @Test
    @WithBookselfUserDetails(id = 2)
    void deleteBookFromBookList_Unauthorized() throws Exception {
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

        mockMvc.perform(delete(apiPrefix + "/" + bookListId + "/books/" + bookId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void deleteBookFromBookList_NotInList_BadRequest() throws Exception {
        var bookList = new BookList();
        bookList.setUserId(authenticatedUserId);

        var book = new Book();
        book.setId(bookId);

        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(bookList));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        mockMvc.perform(delete(apiPrefix + "/" + bookListId + "/books/" + bookId))
            .andExpect(status().isBadRequest());
    }

    @Test
    void giveANonEmptyBookList_whenGetRequestedWithIdOfList_themBooksBelongingToListAreReturned()
            throws Exception {

        final Set<String> bookIds = Set.of("id1", "id2", "id3", "id4");
        final Set<Book> books = bookIds
                .stream()
                .map(id -> {
                    final Book b = new Book();
                    b.setId(id);
                    when(bookRepository.findById(id)).thenReturn(Optional.of(b));
                    return b;
                })
                .collect(Collectors.toSet());

        when(bookListRepository.findAllBookIdInList(bookListId, maxReturnedBooks)).thenReturn(bookIds);

        mockMvc.perform(get(apiPrefix + "/" + bookListId + "/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }
}
