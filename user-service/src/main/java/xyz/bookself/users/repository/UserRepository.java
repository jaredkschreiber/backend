package xyz.bookself.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import xyz.bookself.users.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT r FROM User r WHERE r.email = ?1 AND r.passwordHash = ?2")
    User findUserByCred(String email, String passwordHash);

    @Query("SELECT r FROM User r WHERE r.session = ?1")
    User findUserBySession(String session);
}

