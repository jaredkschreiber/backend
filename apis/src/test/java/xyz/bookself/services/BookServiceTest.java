package xyz.bookself.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.controllers.book.BookDTO;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class BookServiceTest {

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Value("${bookself.api.max-returned-books}")
    private int maxReturnedBooks;

    @Test
    void givenASetOfBooks_thenFindAnyBooksReturnsTheSameNumberOfBookDTOs() {
        final Set<Book> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId("book-" + i);
                    return b;
                })
                .collect(Collectors.toSet());
        when(bookRepository.findAnyBooks(maxReturnedBooks)).thenReturn(books);
        assertThat(bookService.findAnyBooks().size()).isEqualTo(maxReturnedBooks);
    }

    @Test
    void givenASetOfBooks_thenFindBooksByGenreReturnsTheSameNumberOfBookDTOs() {
        final String genre = "Some Genre";
        final Set<Book> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId("book-" + i);
                    b.setGenres(Set.of(genre));
                    return b;
                })
                .collect(Collectors.toSet());
        when(bookRepository.findAllByGenre(genre, maxReturnedBooks)).thenReturn(books);
        assertThat(bookService.findBooksByGenre(genre).size()).isEqualTo(maxReturnedBooks);
    }

    @Test
    void givenASetOfBooks_thenFindBooksByAuthorReturnsTheSameNumberOfBookDTOs() {
        final String authorId = "Some Author Id";
        final Author author = new Author();
        author.setId(authorId);
        final Set<Book> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId("book-" + i);
                    b.setAuthors(Set.of(author));
                    return b;
                })
                .collect(Collectors.toSet());
        when(bookRepository.findAllByAuthor(authorId, maxReturnedBooks)).thenReturn(books);
        assertThat(bookService.findBooksByAuthor(authorId).size()).isEqualTo(maxReturnedBooks);
    }
}
