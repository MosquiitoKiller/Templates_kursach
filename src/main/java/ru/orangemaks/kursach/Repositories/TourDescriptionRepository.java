package ru.orangemaks.kursach.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.orangemaks.kursach.Models.TourDescription;

public interface TourDescriptionRepository extends JpaRepository<TourDescription,Long> {
}
