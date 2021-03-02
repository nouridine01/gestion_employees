package com.example.demo.controllers;

import com.example.demo.dao.CompetenceRepository;
import com.example.demo.domain.Competence;
import com.example.demo.domain.Conge;
import com.example.demo.domain.Employee;
import com.example.demo.domain.Periode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CompetenceController {
    @Autowired
    private CompetenceRepository competenceRepository;

    @RequestMapping(value = "/competences")
    public String index(Model model, @RequestParam(name = "page",defaultValue = "0") int page,
                        @RequestParam(name = "mc",defaultValue = "") String mc,
                        @RequestParam(name = "size",defaultValue = "5")int size) {
        Page<Competence> liste = competenceRepository.chercher("%"+mc+"%", PageRequest.of(page, size) );
        int[] pages = new int[liste.getTotalPages()];
        model.addAttribute("listes", liste.getContent());
        model.addAttribute("pages", pages);
        model.addAttribute("size", size);
        model.addAttribute("pageCourante", page);
        model.addAttribute("mc", mc);
        //retourne la vue employees.html
        return "competences";
    }

    @RequestMapping(value = "/deleteCompetence", method = RequestMethod.GET)
    public String delete(Long id,String mc,String page,String size) {
        competenceRepository.deleteById(id);
        return "redirect:/competences?page="+page+"&mc="+mc+"&size="+size;
    }

    @RequestMapping(value = "/detailCompetence", method = RequestMethod.GET)
    public String detail(Model model,Long id) {
        model.addAttribute("comp",competenceRepository.findById(id).get());
        return "detail_competence";
    }



    @RequestMapping(value = "/formCompetence", method = RequestMethod.GET)
    public String formProduit (Model model) {
        model.addAttribute("comp", new Competence());
        return "form_competence";
    }

    @RequestMapping(value = "/saveCompetence", method = RequestMethod.POST)
    public String save (Model model , @Valid Competence comp, BindingResult br, HttpServletRequest request) {

        if(br.hasErrors()) {
            model.addAttribute("comp",comp);
            return "form_competence";
        }

        Competence c = competenceRepository.save(comp);
        model.addAttribute("comp", c);
        return "detail_competence";
    }

    @RequestMapping(value = "/editCompetence", method = RequestMethod.GET)
    public String edit (Model model,Long id) {
        model.addAttribute("comp", competenceRepository.findById(id).get());
        return "edit_competence";
    }

    @RequestMapping(value = "/updateCompetence", method = RequestMethod.POST)
    public String update (Model model , @Valid Competence c, BindingResult br, HttpServletRequest request) {
        Competence c2 = competenceRepository.findById(c.getId()).get();
        c2.setNom(c.getNom());
        competenceRepository.save(c2);
        model.addAttribute("comp", c2);
        return "detail_competence";
    }
}
