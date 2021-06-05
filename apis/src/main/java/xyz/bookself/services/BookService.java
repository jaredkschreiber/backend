package xyz.bookself.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.controllers.book.BookDTO;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookselfApiConfiguration apiConfiguration;

    @Autowired
    public BookService(BookRepository repository, BookselfApiConfiguration configuration) {
        this.bookRepository = repository;
        this.apiConfiguration = configuration;
    }

    public Collection<BookDTO> findAnyBooks() {
        return bookRepository
                .findAnyBooks(apiConfiguration.getMaxReturnedBooks())
                .stream()
                .map(BookDTO::new)
                .collect(Collectors.toSet());
    }

    public Collection<BookDTO> findBooksByGenre(final String genre) {
        return bookRepository
                .findAllByGenre(genre, apiConfiguration.getMaxReturnedBooks())
                .stream()
                .map(BookDTO::new)
                .collect(Collectors.toSet());
    }

    public Collection<BookDTO> findBooksByAuthor(final String authorId) {
        return bookRepository
                .findAllByAuthor(authorId, apiConfiguration.getMaxReturnedBooks())
                .stream()
                .map(BookDTO::new)
                .collect(Collectors.toSet());
    }
}
