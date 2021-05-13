package xyz.bookself.controllers.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.domain.BookListEnum;
import xyz.bookself.users.repository.BookListRepository;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/book-lists")
@Slf4j
public class BookListController {
    private final BookListRepository bookListRepository;
    @Autowired
    public BookListController(BookListRepository repository) {
        this.bookListRepository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookList> getBookList(@PathVariable String id) {
        final BookList booklist = bookListRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(booklist, HttpStatus.OK);
    }

    @PostMapping("/new-book-lists")
    public ResponseEntity<BookList> generateBookList() {
        final BookList newDNF = new BookList();
        newDNF.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 24));
        newDNF.setListType(BookListEnum.DNF);
        bookListRepository.save(newDNF);
        return new ResponseEntity<>(newDNF, HttpStatus.OK);
    }

    @PostMapping(value = "/add-book-to-list", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BookList> addBookToList(@RequestBody BookIdListIdDTO bookIdListIdDTO) {
        final BookList foundBookList = bookListRepository.findById(bookIdListIdDTO.getListId()).orElseThrow();
        final Set<String> booksInList = foundBookList.getBooks();
        booksInList.add(bookIdListIdDTO.getBookId());
        foundBookList.setBooks(booksInList);
        return new ResponseEntity<>(foundBookList, HttpStatus.OK);
    }
}