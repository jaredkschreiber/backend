package xyz.bookself.controllers.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.security.BookselfUserDetails;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.UserRepository;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/users")
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository repository) {
        this.userRepository = repository;
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
        return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.OK);
    }
}

class NewUserDTO
{ 
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