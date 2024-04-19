package com.example.controller;

import com.example.dao.PerformRepository;
import com.example.dao.PredefenseRepository;
import com.example.model.Defense;
import com.example.model.Perform;
import com.example.model.Predefense;
import com.example.service.CommissionMemberService;
import com.example.service.PerformService;
import com.example.service.PredefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Optional;

@Controller
@RequestMapping(path = "predefenses")
public class PredefenseController {

    @Autowired
    private PerformService performService;
    @Autowired
    private PerformRepository performRepository;
    @Autowired
    private PredefenseService predefenseService;
    @Autowired
    private PredefenseRepository predefenseRepository;
    @Autowired
    private CommissionMemberService commissionMemberService;

    @GetMapping
    public String predefensesPage(Model model) {
        model.addAttribute("predefenses", predefenseService.getAll());
        return "predefenses";
    }

    @GetMapping(path = "/newPredefense")
    public String newPredefensePage(Model model) {
        model.addAttribute("performs", performService.getAll());
        model.addAttribute("commissionMember", commissionMemberService.getAll());
        return "newPredefense";
    }

    @GetMapping(path = "/{id}/edit")
    public String editPredefensePage(@PathVariable(value="id") String id, Model model) {
        Optional<Predefense> optionalPredefense = predefenseRepository.findById(Integer.parseInt(id));
        if (optionalPredefense.isPresent()) {
            Predefense predefense = optionalPredefense.get();
            model.addAttribute("predefense", predefense);
            model.addAttribute("performs", performService.getAll());
            model.addAttribute("commissionMembers", commissionMemberService.getAll());
        }
        return "editPredefense";
    }

    @PostMapping(path = "/newPredefense/submit")
    public String createPredefense(@RequestParam String type,
                                   @RequestParam String performId,
                                   @RequestParam String date,
                                   @RequestParam String mark,
                                   @RequestParam String chairmanFio) {

        Predefense predefense = new Predefense();
        predefense.setType(type);

        Optional<Perform> optionalPerform = performRepository.findById(Integer.parseInt(performId));
        optionalPerform.ifPresent(predefense::setPerform);

        predefense.setDate(Date.valueOf(date));
        predefense.setMark(mark);
        predefense.setChairmanFio(chairmanFio);
        predefenseRepository.save(predefense);
        return "redirect:/predefenses";
    }

    @GetMapping(path = {"/{id}"})
    public String getPredefense(@PathVariable(value = "id") String id, Model model) {
        Optional<Predefense> optionalPredefense = predefenseRepository.findById(Integer.parseInt(id));
        if (optionalPredefense.isPresent()) {
            Predefense predefense = optionalPredefense.get();
            model.addAttribute("predefense", predefense);

            return "predefense";
        } else return "redirect:/predefense";
    }

    @PostMapping(path = "/{id}/edit/submit")
    public String editPredefense(@PathVariable(value = "id") String id,
                                 @RequestParam String type,
                                 @RequestParam String date,
                                 @RequestParam String mark,
                                 @RequestParam String chairmanFio) {

        Optional<Predefense> optionalPredefense = predefenseRepository.findById(Integer.parseInt(id));
        if (optionalPredefense.isPresent()) {
            Predefense predefense = optionalPredefense.get();
            predefense.setType(type);

            predefense.setDate(Date.valueOf(date));
            predefense.setMark(mark);
            predefense.setChairmanFio(chairmanFio);
            predefenseRepository.save(predefense);
            return "redirect:/predefenses/" + predefense.getId();
        } else return "redirect:/predefenses";
    }

    @PostMapping(path = "/delete")
    public String deletePredefense(@RequestParam(value = "deleteButton") String id) {
        try {
            predefenseRepository.deleteById(Integer.parseInt(id));
            System.out.println("The predefense with id " + id + " was deleted");
        } catch (EmptyResultDataAccessException | IllegalArgumentException ignored) {
        }
        return "redirect:/predefenses";
    }
}
