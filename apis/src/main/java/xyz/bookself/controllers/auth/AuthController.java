package xyz.bookself.controllers.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import xyz.bookself.security.BookselfUserDetails;

import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.UserRepository;

@RestController
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository repository) {
        this.userRepository = repository;
    }

    @PostMapping("/signin")
    public ResponseEntity<User> signIn(@AuthenticationPrincipal BookselfUserDetails userDetails) {
        // If nobody is logged in, UNAUTHORIZED
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        log.info(userDetails.getUsername(), userDetails.getPassword());

        return userRepository.findById(userDetails.getId())
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

