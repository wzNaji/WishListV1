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
    public String saveAttraction(@ModelAttribute Item item) {
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
       Optional<Item> item = itemService.findById(id);
        if (item.isPresent()) {
            itemService.deleteItem(item);
            return "redirect:/";
        }

        return "redirect:/errorPage";
    }
}