package xyz.bookself;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Disable CSRF for tests... since we're ignoring the basic auth setup, we need this here so we don't get 403s.
 */
@Profile("test")
@Configuration
public class TestingBookselfAuthConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}
