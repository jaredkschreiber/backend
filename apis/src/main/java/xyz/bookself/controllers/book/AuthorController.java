package xyz.bookself.controllers.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.repository.AuthorRepository;

@RestController
@RequestMapping("/v1/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorController(AuthorRepository repository) {
        this.authorRepository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable String id) {
        final Author author = authorRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(author, HttpStatus.OK);
    }
}
