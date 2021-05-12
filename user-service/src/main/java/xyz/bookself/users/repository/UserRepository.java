package xyz.bookself.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.users.domain.User;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM users LIMIT ?1")
    Collection<User> findUser(int limit);
}