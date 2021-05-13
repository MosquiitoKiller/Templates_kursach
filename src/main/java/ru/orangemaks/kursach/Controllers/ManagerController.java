package ru.orangemaks.kursach.Controllers;

import ru.orangemaks.kursach.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.orangemaks.kursach.Services.EmailService;
import ru.orangemaks.kursach.Services.TourFilter;
import ru.orangemaks.kursach.Services.TourService;
import ru.orangemaks.kursach.Services.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    TourService tourService;
    @Autowired
    UserService userService;
    @Autowired
    TourFilter tourFilter;
    @Autowired
    EmailService emailService;

    User user;

    @GetMapping("")
    public String initPageManager(){
        return "manager";
    }

    @GetMapping("/addUser")
    public String registration(Model model){
        return "registration";
    }

    @PostMapping("/addUser")
    public String addUser(@Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        String answer = userService.saveUser(userForm);
        if (!answer.equals("")){
            model.addAttribute("usernameError", answer);
            return "registration";
        }
        user = userForm;
        return "redirect:/";
    }
}
