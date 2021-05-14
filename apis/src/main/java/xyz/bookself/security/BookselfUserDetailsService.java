package xyz.bookself.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xyz.bookself.users.repository.UserRepository;

/**
 * Used by Spring Security to interface with {@link UserRepository} to grab the user object from the database when authenticating.
 */
@Service
public class BookselfUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public BookselfUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new BookselfUserDetails(user);
    }

}
