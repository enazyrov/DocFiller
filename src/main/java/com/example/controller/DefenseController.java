package com.example.controller;

import com.example.dao.DefenseRepository;
import com.example.dao.PerformRepository;
import com.example.model.Defense;
import com.example.model.Perform;
import com.example.service.CommissionMemberService;
import com.example.service.DefenseService;
import com.example.service.PerformService;
import com.example.utils.FioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
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

    @GetMapping(path = "/{id}/edit")
    public String editDefensePage(@PathVariable(value="id") String id, Model model) {
        Optional<Defense> optionalDefense = defenseRepository.findById(Integer.parseInt(id));
        if (optionalDefense.isPresent()) {
            Defense defense = optionalDefense.get();
            model.addAttribute("defense", defense);
            model.addAttribute("performs", performService.getAll());
            model.addAttribute("commissionMembers", commissionMemberService.getAll());
        }
        return "editDefense";
    }




    @PostMapping(path = "/newDefense/submit")
    public String createPerform(@RequestParam String protocolNumber,
                                @RequestParam String type,
                                @RequestParam String performId,
                                @RequestParam String date,
                                @RequestParam String beginTime,
                                @RequestParam String endTime,
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
        defense.setDate(Date.valueOf(date));
        if (beginTime != null) {
            defense.setBeginTime(beginTime);
        }
        if (endTime != null) {
            defense.setEndTime(endTime);
        }
        defense.setEvaluation(evaluation);
        defense.setMark(mark);

        Optional<Perform> optionalPerform = performRepository.findById(Integer.parseInt(performId));
        optionalPerform.ifPresent(defense::setPerform);

        defense.setChairmanFio(chairmanFio);
        defense.setShortChairmanFio(FioUtils.getShortFio(chairmanFio));
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
        defense.setShortSecretaryFio(FioUtils.getShortFio(secretaryFio));
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
        } else return "redirect:/defenses";
    }

    @PostMapping(path = "/{id}/edit/submit")
    public String editPerform(@PathVariable(value = "id") String id,
                              @RequestParam String type,
                              @RequestParam String date,
                              @RequestParam String beginTime,
                              @RequestParam String endTime,
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
        Optional<Defense> optionalDefense = defenseRepository.findById(Integer.parseInt(id));
        if (optionalDefense.isPresent()) {
            Defense defense = optionalDefense.get();

            defense.setType(type);
            defense.setDate(Date.valueOf(date));
            if (beginTime != null) {
                defense.setBeginTime(beginTime);
            }
            if (endTime != null) {
                defense.setEndTime(endTime);
            }
            defense.setEvaluation(evaluation);
            defense.setMark(mark);

            defense.setChairmanFio(chairmanFio);
            defense.setShortChairmanFio(FioUtils.getShortFio(chairmanFio));
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
            defense.setShortSecretaryFio(FioUtils.getShortFio(secretaryFio));
            defenseRepository.save(defense);
            return "redirect:/defenses/" + defense.getId();
        } else return "redirect:/defenses";
    }


    @PostMapping(path = "/delete")
    public String deleteDefense(@RequestParam(value = "deleteButton") String id) {
        try {
            defenseRepository.deleteById(Integer.parseInt(id));
            System.out.println("The defense with id " + id + " was deleted");
        } catch (EmptyResultDataAccessException | IllegalArgumentException ignored) {
        }
        return "redirect:/defenses";
    }
}
