package com.boefcity.wishlistv1.controllers;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/addForm")
    public String displayAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "addForm";
    }

    @PostMapping("/create")
    public String saveItem(@ModelAttribute Item item) {
        if (item.getName().isEmpty()) {
            return "redirect:/errorPage";
        }
        Item itemToCreate = new Item(item.getId(),
                item.getName(), item.getDescription(), item.getLink());
        itemService.create(itemToCreate);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable int id) {
        if (itemService.findById(id).isPresent()) {
            itemService.deleteById(id);
            return "redirect:/"; // Item found and deleted, redirect to home
        } else {
            return "redirect:/errorPage"; // Item not found, redirect to error page
        }
    }

    @GetMapping("/home")
    public String displayItems(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "home";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        Optional<Item> item = itemService.findById(id);
        if (item.isPresent()) {
            model.addAttribute("itemDetails", item.get());
        }
        else {
            return "errorPage";
        }
        return "editForm";
    }





}