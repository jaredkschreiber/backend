package xyz.bookself.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import xyz.bookself.security.BookselfUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets up the API to require auth on all endpoints but /ping (can't do auth here due to load balancer health checking)
 *
 * Uses the {@link xyz.bookself.security.BookselfUserDetailsService} to check the database for a user and
 * {@link BCryptPasswordEncoder} to deal with comparing the plain text password to the hash we have in the DB.
 */
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
                .antMatchers("/**").permitAll() // Allow everything -- override at the Controller level on a method-by-method basis
                .and()
                .csrf().disable()
                .httpBasic()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/v1/auth/signout", "POST"))
                .addLogoutHandler(new BookselfLogoutHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
    }
}

class BookselfLogoutHandler implements LogoutHandler {
    /**
     * Fix CORS on
     * @param request
     * @param response
     * @param authentication
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        response.setHeader("Access-Control-Allow-Origin", "*");
    }
}
