package com.example.controller;

import com.example.service.DefenseService;
import com.example.service.PerformService;
import com.example.service.PredefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.model.Review;
import com.example.service.ReviewService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping
public class MainController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PerformService performService;

    @Autowired
    private DefenseService defenseService;

    @Autowired
    private PredefenseService predefenseService;

    @RequestMapping(path = "/home")
    public String homePage(Model model) {
        return "home";
    }
    @RequestMapping(path = "/test")
    public String testPage(Model model) {
        return "test";
    }
    @RequestMapping(path = "/print-forms")
    public String printFormsUploadPage(Model model) {
        return "printForms";
    }

    @RequestMapping(path = "/profile")
    public String profilePage(Model model) {
        return "profile";
    }

    @RequestMapping(path = "/feedback")
    public String reviewsPage(Model model) {
        model.addAttribute("reviews", reviewService.getAll());
        return "reviews";
    }

    @RequestMapping(path = "/feedback/editor/submit", method = RequestMethod.POST)
    public String submitReview(@ModelAttribute Review review) {
        reviewService.save(review);
        return "redirect:../";
    }

    @RequestMapping(path = "/login")
    public String loginPage() {
        return "login";
    }
}
