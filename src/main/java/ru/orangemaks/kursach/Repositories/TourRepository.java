package ru.orangemaks.kursach.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.orangemaks.kursach.Models.Tour;

public interface TourRepository extends JpaRepository<Tour,Long> {
    Tour findByStartAndFinishAndDate(String start,String finish, String date);
}
