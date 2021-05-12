package xyz.bookself.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.bookself.security.BookselfUserDetailsService;

/**
 * Sets up the API to require auth on all endpoints but /pint (can't do auth here due to load balancer health checking)
 *
 * Uses the {@link xyz.bookself.security.BookselfUserDetailsService} to check the database for a user and
 * {@link BCryptPasswordEncoder} to deal with comparing the plain text password to the hash we have in the DB.
 */
@Profile("!test")
@Configuration
@EnableWebSecurity
public class BookselfBasicAuthConfiguration extends WebSecurityConfigurerAdapter {

    private final BookselfUserDetailsService userDetailsService;

    public BookselfBasicAuthConfiguration(BookselfUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/ping").permitAll() // Allow calls to /ping
                .antMatchers("/actuator/**").permitAll() // Allow calls to all /actuator endpoints
                .antMatchers("/v1/users/new-user").permitAll() //Allow registration endpoint to be accessed without auth
                .anyRequest().authenticated() // Everything else needs auth -- this'll probably need to be tweaked as we go
                .and()
                .csrf().disable()
                .httpBasic();
    }
}
