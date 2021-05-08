package xyz.bookself.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.bookself.users.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}

