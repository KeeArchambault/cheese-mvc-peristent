package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value="")
    public String index(Model model){

        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value="add", method = RequestMethod.GET)
    public String addMenu(Model model, Menu menu){
        model.addAttribute("menu", menu);
        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String addMenu(Model model, @ModelAttribute @Valid Menu menu, Errors errors){

        if(errors.hasErrors()) {

            return "menu/add";

        }else{
            menuDao.save(menu);
            int id = menu.getId();

            return "redirect:menu/view/" + id;
        }
    }

    @RequestMapping(value = "view/{id}", method=RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id){
        model.addAttribute("menu", menuDao.findOne(id));
        model.addAttribute("title", menuDao.findOne(id).getName());

        return "menu/view";
    }

    @RequestMapping(value="add-item/{id}", method=RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id){

        Menu menu = menuDao.findOne(id);
        String menuName = menu.getName();
        Iterable<Cheese> cheeses = cheeseDao.findAll();
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeses);


        model.addAttribute("form", form);
        model.addAttribute("title", "Add Item to Menu: " + menuName);
        model.addAttribute("cheeses", cheeses);
        model.addAttribute("menu", menu);

        return "menu/add-item";
    }

    @RequestMapping(value="add-item/{id}", method=RequestMethod.POST)
    public String addItem(Model model, @RequestParam  int cheeseId, @RequestParam int id, @ModelAttribute @Valid AddMenuItemForm form, Errors errors){

        if (errors.hasErrors()){

            return "menu/add-item";
        }else{

            Cheese cheese = cheeseDao.findOne(cheeseId);
            Menu menu = menuDao.findOne(id);
            List<Cheese> cheeses = menu.getCheeses();
            model.addAttribute("menu", menuDao.findOne(id));
            model.addAttribute("title", menuDao.findOne(id).getName());


            menu.addItem(cheese);

            menuDao.save(menu);

            return "menu/view";
        }

    }


}
