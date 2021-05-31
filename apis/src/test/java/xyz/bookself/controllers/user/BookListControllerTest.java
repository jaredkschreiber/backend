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
    void givenNewBookListName_whenSubmitChange_thenBookListNameShouldBeChanged()
        throws Exception{
        final String bookListId = "book-list-id";
        final String oldBookListName = "old-book-list-name";
        final String newBookListName = "new-book-list-name";

        final BookList originalBookList = new BookList();
        originalBookList.setId(bookListId);
        originalBookList.setBookListName(oldBookListName);

        final BookList renamedBookList = new BookList();
        renamedBookList.setId(bookListId);
        renamedBookList.setBookListName(newBookListName);

        final ShelfNameDTO shelfNameDTO = new ShelfNameDTO();
        shelfNameDTO.setNewListName(newBookListName);

        final String jsonRequestBody = TestUtilities.toJsonString(shelfNameDTO);

        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(originalBookList));
        when(bookListRepository.save(renamedBookList)).thenReturn(renamedBookList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(apiPrefix + "/" + bookListId + "/update-list-name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(renamedBookList)))
                .andDo(print());

    }

    @Test
    void givenBooksToBeMovedToADifferentList_thenBooksAreMovedToADifferentList() throws Exception{

        final String bookListId = "existing-book-list";
        final String newBookListId = "second-existing-book-list";

        final Set<String> originalSetOfBooks = new HashSet<>(Arrays.asList("book-id-1", "book-id-2", "book-id-3"));
        final Set<String> booksToBeRemoved = new HashSet<>(Arrays.asList("book-id-2", "book-id-3"));
        final Set<String> updatedSetOfBooks = new HashSet<>(Collections.singletonList("book-id-1"));

        final Set<String> setOfBookToBeAddedTo = new HashSet<>(Collections.singletonList("book-id-4"));
        final Set<String> updatedSetOfBooksAfterAdd = new HashSet<>(Arrays.asList("book-id-4", "book-id-2", "book-id-3"));

        final BookList originalBookList = new BookList();
        originalBookList.setId(bookListId);
        originalBookList.setBooks(originalSetOfBooks);

        final BookList expectedBookList = new BookList();
        expectedBookList.setId(bookListId);
        expectedBookList.setBooks(updatedSetOfBooks);

        final BookList originalBookListToBeAddedTo = new BookList();
        originalBookListToBeAddedTo.setId(newBookListId);
        originalBookListToBeAddedTo.setBooks(setOfBookToBeAddedTo);

        final BookList expectedBookListAfterAdd = new BookList();
        expectedBookListAfterAdd.setId(newBookListId);
        expectedBookListAfterAdd.setBooks(updatedSetOfBooksAfterAdd);


        final MoveShelfDTO moveShelfDTO = new MoveShelfDTO();
        moveShelfDTO.setBooksToBeRemoved(booksToBeRemoved);
        moveShelfDTO.setBooksToBeAdded(booksToBeRemoved);
        moveShelfDTO.setNewBookListId(newBookListId);

        final String jsonRequestBody = TestUtilities.toJsonString(moveShelfDTO);

        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(originalBookList));
        when(bookListRepository.findById(newBookListId)).thenReturn(Optional.of(originalBookListToBeAddedTo));

        when(bookListRepository.save(expectedBookListAfterAdd)).thenReturn(expectedBookListAfterAdd);
        when(bookListRepository.save(expectedBookList)).thenReturn(expectedBookList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(apiPrefix + "/" + bookListId + "/move-books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(expectedBookList)))
                .andDo(print());


    }

    @Test
    void givenNameChange_butNoBookMovement_thenNameOfListIsUpdatedAndNoBooksAreMoved() throws Exception{
        final String bookListId = "book-list-id";
        final String oldBookListName = "old-book-list-name";
        final String newBookListName = "new-book-list-name";

        final BookList originalBookList = new BookList();
        originalBookList.setId(bookListId);
        originalBookList.setBookListName(oldBookListName);

        final BookList renamedBookList = new BookList();
        renamedBookList.setId(bookListId);
        renamedBookList.setBookListName(newBookListName);

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setNewListName(newBookListName);
        shelfDto.setNewBookListId(bookListId);

        final String jsonRequestBody = TestUtilities.toJsonString(shelfDto);
        when(bookListRepository.findById(bookListId)).thenReturn(Optional.of(originalBookList));
        when(bookListRepository.save(renamedBookList)).thenReturn(renamedBookList);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(apiPrefix + "/" + bookListId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequestBody);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(renamedBookList)))
                .andDo(print());

    }
}
