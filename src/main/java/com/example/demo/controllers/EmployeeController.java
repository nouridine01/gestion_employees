package com.example.demo.controllers;

import com.example.demo.dao.CompetenceRepository;
import com.example.demo.dao.EmployeeRepository;
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
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompetenceRepository competenceRepository;
    @RequestMapping(value = "/employees")
    public String index(Model model, @RequestParam(name = "page",defaultValue = "0") int page,
                        @RequestParam(name = "mc",defaultValue = "") String mc,
                        @RequestParam(name = "size",defaultValue = "5")int size) {
        Page<Employee> liste = employeeRepository.chercher("%"+mc+"%", PageRequest.of(page, size) );
        int[] pages = new int[liste.getTotalPages()];
        model.addAttribute("listes", liste.getContent());
        model.addAttribute("pages", pages);
        model.addAttribute("size", size);
        model.addAttribute("pageCourante", page);
        model.addAttribute("mc", mc);
        //retourne la vue employees.html
        return "employees";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(Long id,String mc,String page,String size) {
        employeeRepository.deleteById(id);
        return "redirect:/employees?page="+page+"&mc="+mc+"&size="+size;
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(Model model,Long id) {
        model.addAttribute("emp", employeeRepository.findById(id).get());
        return "detail";
    }



    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String formProduit (Model model) {
        List<Competence>   competences = competenceRepository.findAll();
        model.addAttribute("emp", new Employee());
        model.addAttribute("competences", competences);
        model.addAttribute("salaire", 0);
        model.addAttribute("duree", 0);
        return "form";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save (Model model , @Valid Employee emp, BindingResult br, HttpServletRequest request) {
        double salaire =Double.valueOf(request.getParameter("salaire"));
        int duree = Integer.valueOf(request.getParameter("duree"));

        if(br.hasErrors()) {
            //bindingresult collection dans lequel est stocké les erreurs
            List<Competence>   competences = competenceRepository.findAll();
            model.addAttribute("competences", competences);
            model.addAttribute("salaire", salaire);
            model.addAttribute("duree", duree);
            model.addAttribute("emp", emp);
            return "form";
        }
        emp = employeeRepository.save(emp);
        LocalDate d = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<Periode> periodes = new ArrayList<>();
        int i;
        for(i=1;i<=duree;i++){
            Periode p = new Periode();
            p.setEmployee(emp);
            p.setDebut(java.sql.Date.valueOf(d.plusYears(i-1)));
            p.setFin(java.sql.Date.valueOf(d.plusYears(i)));
            p.setSalaire(salaire);
            if(i==1) p.setActuel(true);
            p.setConge(new Conge());
            periodes.add(p);
        }
        emp.setPeriodes(periodes);
        emp.setHire_date(java.sql.Date.valueOf(emp.getHire_date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()));
        employeeRepository.save(emp);
        model.addAttribute("emp", emp);
        return "detail";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit (Model model,Long id) {
        List<Competence>   competences = competenceRepository.findAll();
        model.addAttribute("competences", competences);
        Employee e = employeeRepository.findById(id).get();
        Periode p = new Periode();

        for (Periode pe : e.getPeriodes()) {
            if(pe.isActuel()){
                p = pe;
        }
        }
        model.addAttribute("emp", e);
        model.addAttribute("duree", e.getPeriodes().size());
        model.addAttribute("p", p);

        return "edit";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update (Model model , @Valid Employee emp, BindingResult br, HttpServletRequest request) {
        double note = Double.valueOf(request.getParameter("note"));
        Employee em = employeeRepository.findById(emp.getId()).get();
        emp.setHire_date(em.getHire_date());
        emp.setDepart(em.getDepart());
        Date conge;
        Periode p = new Periode();
        Periode per = new Periode();
        List<Periode> periodes = em.getPeriodes();
        for (Periode pe : periodes) {
            if(pe.isActuel()) {
                p = pe;
                per=pe;
            }
        }
        try {
            if(request.getParameter("conge") != null){
                conge = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("conge"));
                System.out.println("conge : "+conge);
                LocalDate d = conge.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                p.getConge().setDebut(java.sql.Date.valueOf(d));
                p.getConge().setFin(java.sql.Date.valueOf(d.plusDays(25)));
                periodes.remove(per);
                periodes.add(p);
            }
        } catch (ParseException e) {
            System.out.println("erreur du congé");
        }
        double salaire = Double.valueOf(request.getParameter("salaire"));
        p.setSalaire(salaire);
        p.setNote(note);
        emp.setPeriodes(periodes);
        if(br.hasErrors()) {
            List<Competence>   competences = competenceRepository.findAll();
            model.addAttribute("competences", competences);
            model.addAttribute("emp", emp);model.addAttribute("duree", emp.getPeriodes().size());
            model.addAttribute("p", p);

            return "edit";
        }

        /*if(duree != emp.getPeriodes().size()){
            LocalDate d = emp.getHire_date().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            List<Periode> periodes = new ArrayList<>();

            int i;
            for(i=emp.getPeriodes().size();i<=duree;i++){
                Periode p = new Periode();
                p.setDebut(java.sql.Date.valueOf(d.plusYears(i)));
                p.setFin(java.sql.Date.valueOf(d.plusYears(i)));
                periodes.add(p);
            }
            emp.setPeriodes(periodes);
        }*/

        employeeRepository.save(emp);
        model.addAttribute("emp", emp);
        return "detail";
    }



    @RequestMapping(value = "/403")
    public String accessdenied () {

        return "403";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String acceuil() {
        return "employees";
    }




}
