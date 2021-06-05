package xyz.bookself.controllers.book;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.repository.AuthorRepository;
import xyz.bookself.config.BookselfApiConfiguration;

import java.util.Collection;

@RestController
@RequestMapping("/v1/authors")
@Timed
public class AuthorController {

    private final BookselfApiConfiguration apiConfiguration;
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorController(BookselfApiConfiguration configuration, AuthorRepository repository) {
        this.apiConfiguration = configuration;
        this.authorRepository = repository;
    }

    @GetMapping("")
    public ResponseEntity<Collection<Author>> getAnyAuthors() {
        final Collection<Author> authors = authorRepository.findAnyAuthors(apiConfiguration.getMaxReturnedAuthors());
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable String id) {
        final Author author = authorRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @GetMapping("/any")
    public ResponseEntity<Collection<Author>> getAuthors() {
        final Collection<Author> authors = authorRepository.findAnyAuthors(apiConfiguration.getMaxReturnedAuthors());
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }
}
