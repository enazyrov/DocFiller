package com.example.controller;

import com.example.dao.PerformRepository;
import com.example.model.Perform;
import com.example.model.Predefense;
import com.example.service.PerformService;
import com.example.service.PredefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "performs")
public class PermormController {

    @Autowired
    private PerformService performService;

    @Autowired
    private PerformRepository performRepository;

    @GetMapping
    public String performsPage(Model model) {
        model.addAttribute("performs", performService.getAll());
        return "performs";
    }

    @GetMapping(value = {"/{id}"})
    public String getPerform(@PathVariable(value = "id") String id, Model model) {
        Optional<Perform> optionalPerform = performRepository.findById(Integer.parseInt(id));
        if (optionalPerform.isPresent()) {
            Perform perform = optionalPerform.get();
            model.addAttribute("perform", perform);

            return "perform";
        } else return "redirect:/perform";
    }

    @PostMapping(path = "/delete")
    public String deleteDefense(@RequestParam(value="deleteButton") String id) {
        try {
            performRepository.deleteById(Integer.parseInt(id));
            System.out.println("The perform with id " + id + " was deleted");
        } catch (EmptyResultDataAccessException | IllegalArgumentException ignored) {
        }
        return "redirect:/performs";
    }
}
