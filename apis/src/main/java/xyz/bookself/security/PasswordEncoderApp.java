package xyz.bookself.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Helper app that'll hash a password so you can manually add it to the DB with:
 * insert into users (email, username, password_hash) values ('blah', 'blah', 'hashed_blah');
 */
public class PasswordEncoderApp {
    public static void main(String[] args) {
        // In own method so we can get unit test coverage
        System.out.println(run(args));
    }

    static String run(String[] args) {
        if (args.length == 0) {
            System.out.println("No argument");
            return null;
        }
        var encoder = new BCryptPasswordEncoder();
        return encoder.encode(args[0]);
    }

}
