package ru.orangemaks.kursach.Controllers;

import ru.orangemaks.kursach.Models.Tour;
import ru.orangemaks.kursach.Models.TourDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.orangemaks.kursach.Requests.DeleteRequest;
import ru.orangemaks.kursach.Requests.EditRequest;
import ru.orangemaks.kursach.Requests.TourCreateRequest;
import ru.orangemaks.kursach.Requests.TourFilterRequest;
import ru.orangemaks.kursach.Services.TourDescriptionService;
import ru.orangemaks.kursach.Services.TourFilter;
import ru.orangemaks.kursach.Services.TourService;

import javax.validation.Valid;
import java.util.Comparator;

@Controller
public class AdminTourController {
    @Autowired
    TourService tourService;

    @Autowired
    TourDescriptionService tourDescriptionService;

    @Autowired
    TourFilter tourFilter;

    @GetMapping("/admin/add")
    public String initAdd(Model model) {
        return "create";
    }

    @PostMapping("/admin/add")
    public String addTour(@Valid TourCreateRequest tourForm, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("tourError", "Некорректно введены данные");
            return "create";
        }

        if (tourForm.getStart().equals("") ||tourForm.getFinish().equals("")
                ||tourForm.getPrice()<=0||tourForm.getDate().equals("")
                ||tourForm.getCount()<=0||tourForm.getImg().length()<=32||tourForm.getText().length()<=32)
        {
            model.addAttribute("tourError", "Некорректный ввод данных");
            return "create";
        }
        Tour tour = new Tour();
        tour.setStart(tourForm.getStart());
        tour.setFinish(tourForm.getFinish());
        tour.setPrice(tourForm.getPrice());
        tour.setDate(tourForm.getDate());
        tour.setCount(tourForm.getCount());

        TourDescription tourDescription = new TourDescription();
        tourDescription.setImg(tourDescriptionService.parse(tourForm.getImg()));
        tourDescription.setText(tourDescriptionService.parse(tourForm.getText()));

        tourDescriptionService.add(tourDescription);
        tour.setTourDescription(tourDescription);

        if (!tourService.addTour(tour)){
            model.addAttribute("tourError", "Некорректный ввод данных");
            tourDescriptionService.delete(tourDescription);
            return "create";
        }
        else {
            tourDescription.setTour(tour);
            tourDescriptionService.add(tourDescription);
            return "redirect:/admin/add";
        }
    }

    @GetMapping("/admin/find")
    public String initFind(Model model){
        model.addAttribute("FilteredTours",tourService.getAll());
        return "findTour";
    }

    @PostMapping("/admin/find")
    public String findTour(@Valid TourFilterRequest tourFilterForm,
                           Long tourId,
                           @RequestParam String action,
                           BindingResult bindingResult,
                           Model model){
        if(action.equals("filter")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("filterError", "Некорректно введены данные");
                return "findTour";
            }
            model.addAttribute("FilteredTours", tourFilter.filterTour(tourFilterForm.getStart(),
                    tourFilterForm.getFinish(),
                    tourFilterForm.getDate(),
                    tourFilterForm.getCount()));
            return "findTour";
        }
        else{
            return "redirect:/admin/find/view/"+tourId;
        }
    }

    @GetMapping("/admin/find/view/{tourId}")
    public String viewTour(@PathVariable Long tourId, Model model){
        Tour tour = tourService.findById(tourId);
        if(tour!=null){
            model.addAttribute("users",tour.getUsers());
            return "userWithTour";
        }
        else{
            return "redirect:/admin/find";
        }
    }


    @GetMapping("/admin/edit")
    public String initEdit(Model model) {
        return "editTour";
    }

    @PostMapping("/admin/edit")
    public String editTour(@Valid EditRequest editForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editError", "Некорректно введены данные");
            return "editTour";
        }
        if (!tourService.editTour(editForm)){
            model.addAttribute("editError","Некорректный ввод данных");
        }
        else{
            model.addAttribute("editError","Запись изменена");
        }
        return "editTour";
    }

    @GetMapping("/admin/delete")
    public String initDel(Model model) {
        return "deleteTour";
    }

    @PostMapping("/admin/delete")
    public String deleteTour(@Valid DeleteRequest deleteForm, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("deleteError", "Некорректно введены данные");
            return "deleteTour";
        }
        Tour tour = tourService.deleteTour(deleteForm.id);
        if (tour!=null){
            model.addAttribute("deletedTour","ID = "+tour.getId()
                    +" From = " +tour.getStart()
                    +" To = " +tour.getFinish()
                    +" Date = " +tour.getDate()
                    +" Price = " +tour.getPrice()
                    +" Count = " +tour.getCount());
        }
        else model.addAttribute("deletedTour","Not found");
        return "deleteTour";
    }


}
