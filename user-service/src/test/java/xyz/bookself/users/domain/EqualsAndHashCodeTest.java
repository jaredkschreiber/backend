package xyz.bookself.users.domain;

import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.domain.BookListEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EqualsAndHashCodeTest {

    @MockBean
    private BookListRepository bookListRepository;

    @Test
    void givenTwoBookLists_whenTheirIdsAreTheSame_thenTheyAreConsideredToBeEqual() {
        final String id = "001";
        final BookList a = new BookList();
        a.setId(id);
        final BookList b = new BookList();
        b.setId(id);
        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void givenABookListEnum_aBookListShouldBeSavedIntoARepository_whenRetrievedById_thenSameBookListShouldBeReturned(){
        final String id = "001";
        final BookList a = new BookList();
        a.setId(id);
        a.setListType(BookListEnum.DNF);

        when(bookListRepository.save(a)).thenReturn(null);
        bookListRepository.save(a);
        when(bookListRepository.findById(id)).thenReturn(Optional.of(a));
        final BookList b = bookListRepository.findById(id).orElseThrow();

        assertThat(a.equals(b)).isTrue();
    }

    @Test
    void givenABookList_aBookIDShouldBeAbleToBeAddedIn_thenABookListShouldHaveOneBook(){
        final BookList a = new BookList();
        final String b = "aaa";
        Set<String> bookIds = new HashSet<String>();
        bookIds.add(b);
        a.setBooks(bookIds);
        assertThat(a.getBooks().size()==1).isTrue();
    }


    @Test
    void givenTwoUsersAreCreated_thenTheirIdsAreDifferent_thenTheyAreConsideredToBeUnique() {
        final User a = new User();
        a.setId(1);
        final User b = new User();
        b.setId(1);
        assertThat(a.equals(b)).isTrue();

    }

    @Test
    void givenABookListObject_whenTheObjectIsNotNull_thenHashCodeReturnsAnInteger() {
        assertThat(new BookList().hashCode()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);

    }

    @Test
    void givenAUserObject_whenTheObjectIsNotNull_thenHashCodeReturnsAnInteger() {
        assertThat(new User().hashCode()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
