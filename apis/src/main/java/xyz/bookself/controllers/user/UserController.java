package xyz.bookself.controllers.user;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.UserRepository;
import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserController {

    private final UserRepository repository;

    UserController(UserRepository repository) {
      this.repository = repository;
    }

    @PostMapping("/register")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody User loginUser, HttpServletResponse response) {

        User validUser = repository.findUserByCred(loginUser.getEmail(), loginUser.getPasswordHash());
        if (validUser != null){
            String generatedString = UUID.randomUUID().toString();
            validUser.setSession(generatedString);
            repository.save(validUser);
            return new ResponseEntity<String>(validUser.getSession(),HttpStatus.OK); 
        } else {
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(@RequestBody User logoutUser, HttpServletResponse response) {
        User validUser = repository.findUserBySession(logoutUser.getSession());
        if (validUser != null){
            validUser.setSession(null);
            repository.save(validUser);
            return new ResponseEntity<String>(HttpStatus.OK);    
        } else {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST); 
        }
    }
}
