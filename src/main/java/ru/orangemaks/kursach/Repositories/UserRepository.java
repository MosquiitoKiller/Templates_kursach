package ru.orangemaks.kursach.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.orangemaks.kursach.Models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}
