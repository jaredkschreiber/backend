package xyz.bookself.controllers.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import xyz.bookself.controllers.TestUtilities;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookListControllerTest {

    private final String apiPrefix = "/v1/book-lists";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookListRepository bookListRepository;

    @Test
    void givenBookListExists_whenIdIsSuppliedToBookListEndpoint_thenABookListIsReturned()
            throws Exception {
        final String validBookListId = "99999999999";
        final BookList bookListThatExistsInDatabase = new BookList();
        bookListThatExistsInDatabase.setId(validBookListId);

        when(bookListRepository.findById(validBookListId)).thenReturn(Optional.of(bookListThatExistsInDatabase));
        mockMvc.perform(get(apiPrefix + "/" + validBookListId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(validBookListId)));
    }

    @Test
    void givenABookList_whenGetRequestedOnUpdateWithNewName_thenBookListIsRenamed() throws Exception {

        final String bookListId = "existing-book-list";
        final String originalListName = "Original List Name";
        final String newListName = "New List Name";

        final BookList originalBookList = new BookList();
        originalBookList.setId(bookListId);
        originalBookList.setBookListName(originalListName);

        final BookList expectedBookList = new BookList();
        expectedBookList.setId(bookListId);
        expectedBookList.setBookListName(newListName);

        final ShelfDto shelfDto = new ShelfDto();
        shelfDto.setNewListName(newListName);
        final String jsonRequestBody = TestUtilities.toJsonString(shelfDto);

        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(originalBookList));
        when(bookListRepository.save(expectedBookList)).thenReturn(expectedBookList);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(apiPrefix + "/" + bookListId + "/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(expectedBookList)))
                .andDo(print());
    }

    @Test
    void givenABookList_whenGetRequestedOnUpdateWithBooksToBeAdded_thenBooksAreAddedToBookList() throws Exception {

        final String bookListId = "existing-book-list";
        final Set<String> originalSetOfBooks = new HashSet<>(Collections.singletonList("book-id-1"));
        final Set<String> booksToBeAdded = new HashSet<>(Arrays.asList("book-id-2", "book-id-3"));
        final Set<String> updatedSetOfBooks = new HashSet<>(Arrays.asList("book-id-1", "book-id-2", "book-id-3"));

        final BookList originalBookList = new BookList();
        originalBookList.setId(bookListId);
        originalBookList.setBooks(originalSetOfBooks);

        final BookList expectedBookList = new BookList();
        expectedBookList.setId(bookListId);
        expectedBookList.setBooks(updatedSetOfBooks);

        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(originalBookList));
        when(bookListRepository.save(expectedBookList)).thenReturn(expectedBookList);

        final ShelfDto shelfDto = new ShelfDto();
        shelfDto.setBooksToBeAdded(booksToBeAdded);
        final String jsonRequestBody = TestUtilities.toJsonString(shelfDto);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(apiPrefix + "/" + bookListId + "/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(expectedBookList)))
                .andDo(print());
    }

    @Test
    void givenABookList_whenGetRequestedOnUpdateWithBooksToBeRemoved_thenBooksAreRemovedFromBookList() throws Exception {
        final String bookListId = "existing-book-list";
        final Set<String> originalSetOfBooks = new HashSet<>(Arrays.asList("book-id-1", "book-id-2", "book-id-3"));
        final Set<String> booksToBeRemoved = new HashSet<>(Arrays.asList("book-id-2", "book-id-3"));
        final Set<String> updatedSetOfBooks = new HashSet<>(Collections.singletonList("book-id-1"));

        final BookList originalBookList = new BookList();
        originalBookList.setId(bookListId);
        originalBookList.setBooks(originalSetOfBooks);

        final BookList expectedBookList = new BookList();
        expectedBookList.setId(bookListId);
        expectedBookList.setBooks(updatedSetOfBooks);

        final ShelfDto shelfDto = new ShelfDto();
        shelfDto.setBooksToBeRemoved(booksToBeRemoved);

        final String jsonRequestBody = TestUtilities.toJsonString(shelfDto);

        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(originalBookList));
        when(bookListRepository.save(expectedBookList)).thenReturn(expectedBookList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(apiPrefix + "/" + bookListId + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(expectedBookList)))
                .andDo(print());
    }
}
