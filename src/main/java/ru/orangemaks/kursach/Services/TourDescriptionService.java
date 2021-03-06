package ru.orangemaks.kursach.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orangemaks.kursach.Models.TourDescription;
import ru.orangemaks.kursach.Repositories.TourDescriptionRepository;

@Slf4j
@Service
public class TourDescriptionService {
    @Autowired
    TourDescriptionRepository tourDescriptionRepository;

    public void add(TourDescription tourDescription){
        tourDescriptionRepository.save(tourDescription);
    }
    //https://drive.google.com/file/d/13dCjpWflutbu6-9kRlxFd2BJMQAqAE1D/view?usp=sharing
    //  ||
    // \||/
    //  \/
    //https://drive.google.com/uc?export=view&id=13dCjpWflutbu6-9kRlxFd2BJMQAqAE1D
    public String parse(String url){
        log.info("parsing url");
        return "https://drive.google.com/uc?export=view&id="+url.substring(("https://drive.google.com/file/d/").length(),url.length()-"/view?usp=sharing".length());
    }

    public void delete(TourDescription tourDescription){
        tourDescriptionRepository.delete(tourDescription);
        log.info("delete TourDescription " + tourDescription.getId());
    }
}
