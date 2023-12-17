package com.example.controller;

import com.example.dao.DefenseRepository;
import com.example.dao.PerformRepository;
import com.example.model.Defense;
import com.example.model.Predefense;
import com.example.service.DefenseService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "defenses")
public class DefenseController {

    @Autowired
    private DefenseService defenseService;

    @Autowired
    private DefenseRepository defenseRepository;

    @GetMapping
    public String defensesPage(Model model) {
        model.addAttribute("defenses", defenseService.getAll());
        return "defenses";
    }

    @GetMapping(path = {"/{id}"})
    public String getDefense(@PathVariable(value = "id") String id, Model model) {
        Optional<Defense> optionalDefense = defenseRepository.findById(Integer.parseInt(id));
        if (optionalDefense.isPresent()) {
            Defense defense = optionalDefense.get();
            model.addAttribute("defense", defense);

            return "defense";
        } else return "redirect:/defense";
    }

    @PostMapping(path = "/delete")
    public String deleteDefense(@RequestParam(value="deleteButton") String id) {
        try {
            defenseRepository.deleteById(Integer.parseInt(id));
            System.out.println("The defense with id " + id + " was deleted");
        } catch (EmptyResultDataAccessException | IllegalArgumentException ignored) {
        }
        return "redirect:/defenses";
    }
}
