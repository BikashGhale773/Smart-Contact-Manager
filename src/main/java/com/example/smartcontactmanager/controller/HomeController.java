package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import com.example.smartcontactmanager.repository.UserRepositroy;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepositroy userRepositroy;
    @RequestMapping("/")
    //Model model send data from controller to view
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("title", "Register");

        //creating blank user object so that the previous value of user can pass
        model.addAttribute("user", new User());
        return "signup";
    }

    //handler for Sign Up
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    //@ModelAttribute send data from view to controller
    //all the form data will map to user variable except checkbox
    //for checkbox we have to use @RequestParam to get data from view to controller
    //value = agreement should be same from signup page name=agreement
    //BindingResult is to handle validation errors
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){

        try{
            if(!agreement){
                System.out.println("You have not agreed the terms and condition");
                throw new Exception("You have not agreed the terms and condition");
            }

            if(result1.hasErrors()){
                System.out.println("Error" + result1.toString());
                //data comes from form goes back to form again
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.jpeg");
            user.setPassword(passwordEncoder.encode(user.getPassword()));


            System.out.println("Agreement:" + agreement);
            System.out.println("User:" + user);

            //data input by user will be seen back to form again

            User result = userRepositroy.save(user);



            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully registered!!!", "alert-success"));
            //session.removeAttribute("message");

            //      old          <th:block th:text="${#session.removeAttribute('message')}"></th:block>
              // new              <div th:text="${@sessionUtilityBean.removemessageFromSession()}"></div>

            return "signup";

        }catch(Exception e){
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!!! " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }

    //handler for login
    @GetMapping("/signin")
    public String CustomLogin(Model model){
        model.addAttribute("title", "Login page");
        return "login";
    }
}
