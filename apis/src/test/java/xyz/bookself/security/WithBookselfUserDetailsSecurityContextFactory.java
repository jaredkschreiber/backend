package xyz.bookself.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithBookselfUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithBookselfUserDetails> {
    @Override
    public SecurityContext createSecurityContext(WithBookselfUserDetails annotation) {
        var bookselfUserDetails = new BookselfUserDetails(annotation.id());
        var authentication = new UsernamePasswordAuthenticationToken(bookselfUserDetails, "", bookselfUserDetails.getAuthorities());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
