package ru.orangemaks.kursach.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.orangemaks.kursach.Models.Tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@Service
@RequiredArgsConstructor
@ManagedResource(objectName = "MyMBeans:category=MBeans,name=SchedulerService")
public class SchedulerService {
    @Autowired
    TourService tourService;
    @Autowired
    TourDescriptionService tourDescriptionService;

    @Scheduled(cron = "0 0 0 0/1 * *")//fixedRate=60000
    @ManagedOperation(description = "Deleting unused tours with an expired date")
    public void deleteTour() throws ParseException {
        List<Tour> tours = tourService.getAll();
        Date currentDate = new Date();
        Date twoDate;
        log.info("======SchedulerService======");
        for (Tour t : tours) {
            twoDate = new SimpleDateFormat("yyyy-MM-dd").parse(t.getDate());
            log.info("currentDate = "+currentDate+"\nTourDate = "+twoDate);
            if (twoDate.before(currentDate)){
                if(t.getUsers()!=null){
                    t.setCount(0);
                    tourService.save(t);
                }
                else {
                    tourDescriptionService.delete(t.getTourDescription());
                    tourService.deleteTour(t.getId());
                }
            }
        }
    }
}
