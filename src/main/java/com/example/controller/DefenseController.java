package com.example.controller;

import com.example.dao.DefenseRepository;
import com.example.dao.PerformRepository;
import com.example.model.Defense;
import com.example.model.Perform;
import com.example.model.Predefense;
import com.example.service.CommissionMemberService;
import com.example.service.DefenseService;
import com.example.service.PerformService;
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

    @Autowired
    private PerformService performService;
    @Autowired
    private PerformRepository performRepository;

    @Autowired
    private CommissionMemberService commissionMemberService;

    @GetMapping
    public String defensesPage(Model model) {
        model.addAttribute("defenses", defenseService.getAll());
        return "defenses";
    }

    @GetMapping(path = "/newDefense")
    public String newDefensePage(Model model) {
        model.addAttribute("performs", performService.getAll());
        model.addAttribute("commissionMembers", commissionMemberService.getAll());
        return "newDefense";
    }


    @PostMapping(path = "/newDefense/submit")
    public String createPerform(@RequestParam String protocolNumber,
                                @RequestParam String type,
                                @RequestParam String performId,
                                @RequestParam String evaluation,
                                @RequestParam String mark,
                                @RequestParam String chairmanFio,
                                @RequestParam String directionNumber,
                                @RequestParam String duration,
                                @RequestParam String pages,
                                @RequestParam String slides,
                                @RequestParam String originality,
                                @RequestParam String reviewerFio,
                                @RequestParam String reviewerMark,
                                @RequestParam String supervisorMark,
                                @RequestParam String otherDocuments,
                                @RequestParam String secretaryFio) {

        Defense defense = new Defense();
        defense.setProtocolNumber(Integer.valueOf(protocolNumber));
        defense.setType(type);
        defense.setEvaluation(evaluation);
        defense.setMark(mark);

        Optional<Perform> optionalPerform = performRepository.findById(Integer.parseInt(performId));
        optionalPerform.ifPresent(defense::setPerformId);

        defense.setChairmanFio(chairmanFio);
        defense.setDirectionNumber(directionNumber);
        defense.setDuration(Integer.valueOf(duration));
        defense.setPages(Integer.valueOf(pages));
        defense.setSlides(Integer.valueOf(slides));
        defense.setOriginality(Integer.valueOf(originality));
        defense.setReviewerFio(reviewerFio);
        defense.setReviewerMark(reviewerMark);
        defense.setSupervisorMark(supervisorMark);
        defense.setOtherDocuments(otherDocuments);
        defense.setSecretaryFio(secretaryFio);
        defenseRepository.save(defense);
        return "redirect:/defenses";
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
