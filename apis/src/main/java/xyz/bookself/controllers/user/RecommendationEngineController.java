package xyz.bookself.controllers.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.RatingRepository;
import xyz.bookself.controllers.book.BookDTO;
import xyz.bookself.exceptions.UnauthorizedException;
import xyz.bookself.security.BookselfUserDetails;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/recommendations")
@Slf4j
public class RecommendationEngineController {
    private final BookListRepository bookListRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    @Autowired
    public RecommendationEngineController(BookListRepository repository,
                                          BookRepository bookRepository,
                                          UserRepository userRepository ) {
        this.bookListRepository = repository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection<BookDTO>> getRecommendation(@PathVariable("id") Integer userId,
                                                                 @AuthenticationPrincipal BookselfUserDetails userDetails,
                                                                 @RequestParam(name = "recommend-by") String recommendBy) {

        throwIfUserDoesNotExist(userDetails);

        final Collection<String> readBookListId = bookListRepository.findAllBooksInUserReadBookList(userId);

        if(readBookListId.size() != 0)
        {
            if(recommendBy != null)
            {
                final Collection<String> informationCollection = new HashSet<>();
                if(recommendBy.equalsIgnoreCase("author"))
                {
                    //recommend by author
                    Set<Author> foundAuthors;
                    for(String bookId : readBookListId) {
                        foundAuthors = bookRepository.findById(bookId).orElseThrow().getAuthors();
                        for(Author author: foundAuthors)
                        {
                            informationCollection.add(author.getId());
                        }
                    }

                    String authorId = informationCollection.stream().findAny().get();
                    //go through each author and
                    //bookRepository.findAllByAuthor(genre, 1).stream().map(BookDTO::new)
                    //add to collection
                    final var books = bookRepository.findAllByAuthor(authorId, 5)
                            .stream().map(BookDTO::new).collect(Collectors.toSet());
                    return new ResponseEntity<>(books, HttpStatus.OK);
                }
                else if (recommendBy.equalsIgnoreCase("genre"))
                {
                    //need all the genres that user had read
                    Set<String> genreList;
                    for(String bookId : readBookListId) {
                        genreList = bookRepository.findById(bookId).orElseThrow().getGenres();
                        informationCollection.addAll(genreList);
                    }

                    String genre = informationCollection.stream().findAny().get();
                    //go through each genre and
                    //bookRepository.findAllByGenre(genre, 1).stream().map(BookDTO::new)
                    //add to collection
                    //min recommendation number is 5
                    //temporary placeholder code.
                    final var books = bookRepository.findAllByGenre(genre, 5)
                            .stream().map(BookDTO::new).collect(Collectors.toSet());
                    return new ResponseEntity<>(books, HttpStatus.OK);
                }
            }


        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private void throwIfUserDoesNotExist(@AuthenticationPrincipal BookselfUserDetails userDetails) {
        if (userDetails == null || !userRepository.existsById(userDetails.getId())) {
            throw new UnauthorizedException();
        }
    }
}
