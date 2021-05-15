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
import org.springframework.web.bind.annotation.*;
import xyz.bookself.security.BookselfUserDetails;

import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

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
    public ResponseEntity<User>createNewUser(@RequestBody NewUserDTO newUserDTO){
        User newUser = new User();

        newUser.setUsername(newUserDTO.getUsername());
        newUser.setEmail(newUserDTO.getEmail());
        newUser.setPasswordHash(newUserDTO.getPasswordHash());
        newUser.setCreated(LocalDate.now());
        userRepository.save(newUser);

        createNewBookLists(newUser);

        return new ResponseEntity<>(newUser, HttpStatus.OK);
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

class NewUserDTO { 
    private String username;
    private String passwordHash;
    private String email;

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
