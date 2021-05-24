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

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import xyz.bookself.security.BookselfUserDetails;

import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/v1/users")
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final BookListRepository bookListRepository;

    @Autowired
    public UserController(UserRepository repository, BookListRepository bookListRepository) {
        this.userRepository = repository;
        this.bookListRepository = bookListRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id,
                                        @AuthenticationPrincipal BookselfUserDetails userDetails) {
        // If nobody is logged in, UNAUTHORIZED
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // If somebody is logged in but is trying to access somebody else's profile, FORBIDDEN
        if (!userDetails.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Otherwise look up the user
        return userRepository.findById(id)
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/new-user", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User>createNewUser(@RequestBody UserDto userDto){

        //check to see if username/email already exists and is non unique.
        //if it exists, return a 409 error
        boolean x = userRepository.findUserByUsername(userDto.getUsername()).isPresent();
        boolean y = userRepository.findUserByEmail(userDto.getEmail()).isPresent();
        if (!(x || y)) {
            User newUser = new User();
            newUser.setUsername(userDto.getUsername());
            newUser.setEmail(userDto.getEmail());
            var passwordHasher = new BCryptPasswordEncoder();
            String hashedPass = passwordHasher.encode(userDto.getPassword());
            newUser.setPasswordHash(hashedPass);
            newUser.setCreated(LocalDate.now());
            userRepository.save(newUser);
    
            createNewBookLists(newUser);
    
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    /**
     * Get all lists user owns
     * @param id user id
     * @return list of shelves owned by user
     */
    @GetMapping("/{id}/book-lists")
    public ResponseEntity<Collection<BookList>> getBookLists(@PathVariable Integer id) {
        final Collection<BookList> lists = bookListRepository.findUserBookLists(id);
        return new ResponseEntity<>(lists, HttpStatus.OK);
    }

    private void createNewBookLists(User newUser) {
        final BookList newDNF = new BookList();
        newDNF.setId(createUUID());
        newDNF.setListType(BookListEnum.DNF);
        newDNF.setUserId(newUser.getId());
        bookListRepository.save(newDNF);

        final BookList read = new BookList();
        read.setId(createUUID());
        read.setListType(BookListEnum.READ);
        read.setUserId(newUser.getId());
        bookListRepository.save(read);

        final BookList current = new BookList();
        current.setId(createUUID());
        current.setListType(BookListEnum.READING);
        current.setUserId(newUser.getId());
        bookListRepository.save(current);
    }

    private String createUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
}

