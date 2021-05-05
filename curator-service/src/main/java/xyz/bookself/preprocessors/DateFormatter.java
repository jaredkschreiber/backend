package xyz.bookself.preprocessors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.entities.ScrapedAuthor;
import xyz.bookself.entities.ScrapedBook;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class DateFormatter {

    public Book transformToBook(ScrapedBook scrapedBook) {
        final Book book = new Book();
        book.setId(scrapedBook.getId());
        book.setTitle(scrapedBook.getTitle());
        book.setPages(scrapedBook.getPages());
        book.setGenres(scrapedBook.getGenres());
        book.setBlurb(scrapedBook.getBlurb());
        book.setAuthors(transformToAuthor(scrapedBook.getAuthors()));
        book.setPublished(transformToLocalDate(scrapedBook.getPublished()));
        return book;
    }

    private Set<Author> transformToAuthor(final Set<ScrapedAuthor> scrapedAuthors) {
        final Set<Author> authors = new HashSet<>();
        scrapedAuthors.stream().map(s -> {
            final Author a = new Author();
            a.setName(s.getName());
            a.setId(s.getId());
            a.setBooks(new HashSet<>());
            return a;
        }).forEach(authors::add);
        return authors;
    }

    private LocalDate transformToLocalDate(String dateString) {
        log.info(dateString);
        return LocalDate.now();
    }
}
