package xyz.bookself.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.users.domain.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM password_reset_tokens WHERE LOWER(token)=LOWER(?1) LIMIT 1")
    Optional<PasswordResetToken> findByToken(String token);

}