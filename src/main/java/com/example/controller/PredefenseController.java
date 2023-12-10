package com.example.controller;

import com.example.dao.PredefenseRepository;
import com.example.model.Predefense;
import com.example.service.PredefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping(path = "predefenses")
public class PredefenseController {

    @Autowired
    private PredefenseService predefenseService;

    @Autowired
    private PredefenseRepository predefenseRepository;

    @GetMapping
    public String predefensesPage(Model model) {
        model.addAttribute("predefenses", predefenseService.getAll());
        return "predefenses";
    }

    @GetMapping(value = {"/{id}"})
    public String getPredefense(@PathVariable(value = "id") String id, Model model) {
        Optional<Predefense> optionalPredefense = predefenseRepository.findById(Integer.parseInt(id));
        if (optionalPredefense.isPresent()) {
            Predefense predefense = optionalPredefense.get();
            model.addAttribute("predefense", predefense);

            return "predefense";
        } else return "redirect:/predefense";
    }
}
