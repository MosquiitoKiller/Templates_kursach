package ru.orangemaks.kursach.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.orangemaks.kursach.Models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
