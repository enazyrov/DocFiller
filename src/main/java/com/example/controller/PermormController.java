package com.example.controller;

import com.example.dao.PerformRepository;
import com.example.model.Defense;
import com.example.model.Perform;
import com.example.service.CommissionMemberService;
import com.example.service.PerformService;
import com.example.utils.FioUtils;
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

    @Autowired
    private CommissionMemberService commissionMemberService;

    @GetMapping
    public String performsPage(Model model) {
        model.addAttribute("performs", performService.getAll());
        return "performs";
    }

    @GetMapping(path = "/newPerform")
    public String newPerformPage(Model model) {
        model.addAttribute("commissionMembers", commissionMemberService.getAll());
        return "newPerform";
    }

    @GetMapping(path = "/{id}/edit")
    public String editPerformPage(@PathVariable(value="id") String id, Model model) {
        Optional<Perform> optionalPerform = performRepository.findById(Integer.parseInt(id));
        if (optionalPerform.isPresent()) {
            Perform perform = optionalPerform.get();
            model.addAttribute("perform", perform);
            model.addAttribute("performs", performService.getAll());
            model.addAttribute("commissionMembers", commissionMemberService.getAll());
        }
        return "editPerform";
    }

    /*@RequestMapping(path = "/editor/submit", method = RequestMethod.POST)
    public String submitPerform(@ModelAttribute Perform perform) {
        performService.save(perform);
        return "redirect:/performs";
    }*/

    @PostMapping(path = "/newPerform/submit")
    public String createPerform(@RequestParam String type,
                                @RequestParam String fullFio,
                                @RequestParam String shortFioGen,
                                @RequestParam String groupNumber,
                                @RequestParam String topic,
                                @RequestParam String advisorFioProtocol,
                                @RequestParam String supervisorFio,
                                @RequestParam String supervisorFioProtocol,
                                @RequestParam String supervisorFioReport) {

        Perform perform = new Perform();
        perform.setType(type);
        perform.setFullFio(fullFio);
        perform.setShortFioGen(shortFioGen);
        perform.setShortFio(FioUtils.getShortFio(fullFio));
        perform.setGroupNumber(Integer.valueOf(groupNumber));
        perform.setTopic(topic);
        perform.setAdvisorFioProtocol(advisorFioProtocol);
        perform.setSupervisorFio(supervisorFio);
        perform.setSupervisorFioProtocol(supervisorFioProtocol);
        perform.setSupervisorFioReport(supervisorFioReport);
        performRepository.save(perform);
        return "redirect:/performs";
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

    @PostMapping(path = "/{id}/edit/submit")
    public String editPerform(@PathVariable(value = "id") String id,
                              @RequestParam String type,
                              @RequestParam String fullFio,
                              @RequestParam String shortFioGen,
                              @RequestParam String groupNumber,
                              @RequestParam String topic,
                              @RequestParam String advisorFioProtocol,
                              @RequestParam String supervisorFio,
                              @RequestParam String supervisorFioProtocol,
                              @RequestParam String supervisorFioReport) {

        Optional<Perform> optionalPerform = performRepository.findById(Integer.parseInt(id));
        if (optionalPerform.isPresent()) {
            Perform perform = optionalPerform.get();
            perform.setType(type);
            perform.setFullFio(fullFio);
            perform.setShortFioGen(shortFioGen);
            perform.setShortFio(FioUtils.getShortFio(fullFio));
            perform.setGroupNumber(Integer.valueOf(groupNumber));
            perform.setTopic(topic);
            perform.setAdvisorFioProtocol(advisorFioProtocol);
            perform.setSupervisorFio(supervisorFio);
            perform.setSupervisorFioProtocol(supervisorFioProtocol);
            perform.setSupervisorFioReport(supervisorFioReport);
            performRepository.save(perform);
            return "redirect:/performs/" + perform.getId();
        } else return "redirect:/performs";
    }

    @PostMapping(path = "/delete")
    public String deleteDefense(@RequestParam(value = "deleteButton") String id) {
        try {
            performRepository.deleteById(Integer.parseInt(id));
            System.out.println("The perform with id " + id + " was deleted");
        } catch (EmptyResultDataAccessException | IllegalArgumentException ignored) {
        }
        return "redirect:/performs";
    }
}
