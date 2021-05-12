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
}
