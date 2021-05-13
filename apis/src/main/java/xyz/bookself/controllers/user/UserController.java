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
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        final User user = userRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(user, HttpStatus.OK);
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