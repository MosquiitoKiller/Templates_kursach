package ru.orangemaks.kursach.Controllers;

import ru.orangemaks.kursach.Models.Tour;
import ru.orangemaks.kursach.Models.User;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.orangemaks.kursach.Repositories.UserRepository;
import ru.orangemaks.kursach.Requests.ByRequest;
import ru.orangemaks.kursach.Requests.EditRequest;
import ru.orangemaks.kursach.Requests.TourFilterRequest;
import ru.orangemaks.kursach.Services.EmailService;
import ru.orangemaks.kursach.Services.TourFilter;
import ru.orangemaks.kursach.Services.TourService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.io.*;
import java.net.URL;
import java.util.Set;

@Controller
public class ClientController {
    @Autowired
    TourService tourService;
    @Autowired
    TourFilter tourFilter;
    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;

    @GetMapping("/booking")
    public String initBooking(Model model) {
        model.addAttribute("FilteredTours",tourService.getAll());
        return "booking";
    }

    @PostMapping("/booking")
    public String findTours(TourFilterRequest tourFilterForm,
                            Long tourId,
                            @RequestParam String action,
                            BindingResult bindingResult,
                            Model model){
        System.out.println(tourFilterForm);
        if(action.equals("filter")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("filterError", "Некорректно введены данные");
                return "booking";
            }
            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(),
                    tourFilterForm.getFinish(),
                    tourFilterForm.getDate(),
                    tourFilterForm.getCount()));
            return "booking";
        }
        else if(action.equals("break")){
            model.addAttribute("FilteredTours",tourService.getAll());
            return "booking";
        }
        else{
            return "redirect:/booking/view/"+tourId;
        }
    }

    @SneakyThrows
    @GetMapping("/booking/view/{tourId}")
    public String viewTour(@PathVariable Long tourId, Model model){
        Tour tourFromDb = tourService.findById(tourId);
        if (tourFromDb!=null) {
            model.addAttribute("image", tourFromDb.getTourDescription().getImg());
            String characteristics = "Откуда: " + tourFromDb.getStart()
                    + "\nКуда: " + tourFromDb.getFinish()
                    + "\nЦена: " + tourFromDb.getPrice()
                    + "\nДата: " + tourFromDb.getDate();
            model.addAttribute("characteristics", characteristics);
            URL text = new URL(tourFromDb.getTourDescription().getText());
            String inputLine, res = "";
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream()));
                while ((inputLine = in.readLine()) != null) {
                    res += inputLine;
                }
                in.close();
            } catch (IOException e) {
                System.out.println("Текстовик поломався(");
            }
            model.addAttribute("description", res);
            return "viewingTour";
        }
        else return "redirect:/booking";
    }

    @SneakyThrows
    @PostMapping("/booking/view/{tourId}")
    public String byTour(@PathVariable Long tourId,
                         @Valid ByRequest byRequest,
                         Model model,
                         @AuthenticationPrincipal User user){
        Tour tourFromDb = tourService.findById(tourId);
        if (tourFromDb!=null) {
            if (byRequest.getMinusCount()<=tourFromDb.getCount()&&byRequest.getMinusCount()>0) {
                tourService.editTour(new EditRequest(tourFromDb.getId(), "", "", "", 0, tourFromDb.getCount() - byRequest.getMinusCount(), "", ""));
                emailService.send(user.getEmail(),"Количество приобретенных вами путевок: "
                    + byRequest.getMinusCount()
                    +"\nОписание путевки: \nИз: " + tourFromDb.getStart()
                    + "\nВ: " + tourFromDb.getFinish()
                    + "\nДата: " + tourFromDb.getDate()
                    + "\nОбщая стоимость: " + tourFromDb.getPrice()*byRequest.getMinusCount() + " рублей");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
                boolean exist = false;
                for (Tour t : userRepository.findById(user.getId()).get().getTours()) {
                    if (t.getId().equals(tourFromDb.getId())) exist = true;
                    System.out.println(t.getId());
                }
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!"+exist);
                if (!exist) {
                    Set<Tour> tourSet = user.getTours();
                    tourSet.add(tourFromDb);
                    for (Tour t : tourSet) {
                        System.out.println(t.getId());
                    }
                    user.setTours(tourSet);
                    userRepository.save(user);
                }
                return "redirect:/booking";
            }
            else {
                model.addAttribute("image",tourFromDb.getTourDescription().getImg());
                String characteristics = "Откуда: " + tourFromDb.getStart()
                        + "\nКуда: " + tourFromDb.getFinish()
                        + "\nЦена: " + tourFromDb.getPrice()
                        + "\nДата: " + tourFromDb.getDate();
                model.addAttribute("characteristics", characteristics);
                URL text = new URL(tourFromDb.getTourDescription().getText());
                String inputLine, res = "";
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(text.openStream()));
                    while ((inputLine = in.readLine()) != null) {
                        res += inputLine;
                    }
                    in.close();
                } catch (IOException e) {
                    System.out.println("Текстовик поломався(");
                }
                model.addAttribute("description", res);
                model.addAttribute("ByError", "Введено некорректное число путевок");
                return "viewingTour";
            }
        }
        else return "redirect:/booking";
    }
}
