package ru.orangemaks.kursach.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.orangemaks.kursach.Models.User;
import ru.orangemaks.kursach.Services.UserService;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/lk")
    public String lk(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("nickname",user.getUsername());
        model.addAttribute("mail",user.getEmail());
        model.addAttribute("name",user.getFirstname());
        model.addAttribute("lastname",user.getLastname());
        model.addAttribute("roles",user.getRoles());
        model.addAttribute("count",user.getTours().size());
        model.addAttribute("usersTours",user.getTours());
        return "lk";
    }
}
