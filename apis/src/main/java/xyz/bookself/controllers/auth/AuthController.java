package xyz.bookself.controllers.auth;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.security.BookselfUserDetails;

import xyz.bookself.users.domain.PasswordResetToken;
import xyz.bookself.users.domain.User;
import xyz.bookself.users.repository.PasswordResetTokenRepository;
import xyz.bookself.users.repository.UserRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
@Slf4j
@Timed
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository tokenRepository;

    private final BookselfApiConfiguration apiConfiguration;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    public AuthController(BookselfApiConfiguration configuration,
                          UserRepository userRepository,
                          PasswordResetTokenRepository tokenRepository) {
        this.apiConfiguration = configuration;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
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

    @PostMapping(value="/forgot-password", consumes={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) throws MessagingException {
        User user = userRepository.findUserByEmail(forgotPasswordDto.getEmail()).get();

        // Check if email exists or bad request
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Create reset token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setCreated(LocalDate.now());
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        // Create the email
        MimeMessage message = emailSender.createMimeMessage();
        message.setSubject("Book Self - Reset Password");
        message.setFrom("bookselfservice@gmail.com");
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setText("Hi <strong>"+ user.getUsername() +"</strong>,<br/><br/>" +
                            "To reset your password, please use the following link:<br/><br/>" +
                            "<a href='"+apiConfiguration.getAppUrl()+"/reset-password/"+resetToken.getToken()+"'>"+apiConfiguration.getAppUrl()+"/reset-password/"+resetToken.getToken()+"</a><br/><br/>" +
                            "Thanks!<br/>" +
                            "Book Self", true);

        // Send the email
        emailSender.send(message);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/reset-password", consumes={MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        PasswordResetToken resetToken = tokenRepository.findByToken(resetPasswordDto.getToken()).get();

        // Check if token exists or bad request
        if (resetToken == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Check if token not expired or forbidden
        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(resetToken.getUser().getId()).get();

        // Check if user exists or bad request
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Update password
        String hashedPass = new BCryptPasswordEncoder().encode(resetPasswordDto.getPassword());
        user.setPasswordHash(hashedPass);
        userRepository.save(user);

        // Delete token
        tokenRepository.deleteById(resetToken.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

