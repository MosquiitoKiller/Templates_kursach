package ru.orangemaks.kursach.Services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orangemaks.kursach.Models.Tour;
import ru.orangemaks.kursach.Models.User;
import ru.orangemaks.kursach.Repositories.TourRepository;
import ru.orangemaks.kursach.Repositories.UserRepository;
import ru.orangemaks.kursach.Requests.EditRequest;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Transactional
public class TourService {
    @Autowired
    TourRepository tourRepository;

    @Autowired
    TourDescriptionService tourDescriptionService;

    @Autowired
    UserRepository userRepository;

    @SneakyThrows
    public boolean addTour(Tour tour) {
        Date currentDate = new Date();
        Date twoDate = new SimpleDateFormat("yyyy-MM-dd").parse(tour.getDate());
        if (twoDate.before(currentDate)||tour.getPrice()<=0||tour.getCount()<0){
            log.info("incorrect add tour");
            return false;
        }
        else {
            tourRepository.save(tour);
            return true;
        }
    }

    public void save(Tour tour){
        tourRepository.save(tour);
    }

    @SneakyThrows
    public boolean editTour(EditRequest editRequest){
        log.info("edit tour");
        if(editRequest.getId()==null){
            log.info("empty field id");
            return false;
        }
        Optional<Tour> tour = tourRepository.findById(editRequest.getId());
        System.out.println(editRequest.toString());
        boolean editTour=false;
        boolean editDescr=false;
        if(tour.isEmpty()){
            log.info("Not found tour with id="+editRequest.getId());
            return false;
        }
        if (!editRequest.getStart().equals("")){
            tour.get().setStart(editRequest.getStart());
            editTour=true;
        }
        if (!editRequest.getFinish().equals("")){
            tour.get().setFinish(editRequest.getFinish());
            editTour=true;
        }
        if (!(editRequest.getPrice()<=0)){
            tour.get().setPrice(editRequest.getPrice());
            editTour=true;
        }
        if (!editRequest.getDate().equals("")){//<0
            Date currentDate = new Date();
            Date twoDate = new SimpleDateFormat("yyyy-MM-dd").parse(editRequest.getDate());
            if (twoDate.before(currentDate)){
                log.info("incorrect date");
                return false;
            }
            else {
                tour.get().setDate(editRequest.getDate());
                editTour = true;
            }
        }
        if (!(editRequest.getCount()<0)){
            tour.get().setCount(editRequest.getCount());
            //if(editRequest.getCount()==0) nullCount=true;
            editTour=true;
        }
        if(!editRequest.getImg().equals("")){
            tour.get().getTourDescription().setImg(tourDescriptionService.parse(editRequest.getImg()));
            editDescr=true;
        }
        if(!editRequest.getText().equals("")){
            tour.get().getTourDescription().setText(tourDescriptionService.parse(editRequest.getText()));
            editDescr=true;
        }
        if (editTour) tourRepository.save(tour.get());
        if (editDescr) tourDescriptionService.add(tour.get().getTourDescription());

        return true;
    }

    public Tour findById(Long id){
        Optional<Tour> tour = tourRepository.findById(id);
        return tour.get();
    }

    public Tour deleteTour(Long id){
        log.info("delete tour");
        if (id == null) return null;
        Optional<Tour> tourOP = tourRepository.findById(id);
        if(tourOP.isEmpty()){
            log.info("Not found tour with id="+id);
            return null;
        }
        Tour tour = tourOP.get();
        tourDescriptionService.delete(tour.getTourDescription());//delete dependency with TourDescription

        User[] users = tour.getUsers().toArray(new User[0]);//delete dependency with User
        for (int i = 0; i < users.length; ++i){
            System.out.println(users[i].getId());
            Set<Tour> tours = users[i].getTours();
            tours.remove(tour);
            users[i].setTours(tours);
            userRepository.save(users[i]);
        }

        tourRepository.deleteById(id);
        return tour;
    }

    public List<Tour> getAll(){
        List<Tour> tours = tourRepository.findAll();
        tours.sort((o1, o2) -> {
            for (int i=0;i<o1.getDate().length();++i){
                if (o1.getDate().toCharArray()[i]<o2.getDate().toCharArray()[i]) return -1;
                else if (o1.getDate().toCharArray()[i]>o2.getDate().toCharArray()[i]) return 1;
            }
            return 0;
        });
        return tours;
    }
}
