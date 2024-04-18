package com.boefcity.wishlistv1.controllers;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.UserService;
import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }
//Navationsmetoder
    @GetMapping("/")
    public String displayHomePage() {
        return "homePage";
    }

    @GetMapping("/login")
    public String displayLoginForm() {
        return "loginForm";
    }
    @GetMapping("/register")
    public String displayRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "registerForm";
    }
    @GetMapping("/addForm")
    public String displayAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "addForm";
    }
    @GetMapping("/editItem/{id}")
    public String displayEditForm(@PathVariable int id,
                                  Model model) {

        Item item = itemService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id: " + id));
        model.addAttribute("item", item);
        return "editForm";
    }

//Oprettelse af User-metoder

    @PostMapping("/register")
    /*ModelAttribute bruges for at skabe et objekt vi kan tilknytte attributer til
    RedirectAttributes bruges til at vise beskeder til brugeren, baseret på fejlhåndtering
     */
    public String createUser(@ModelAttribute User user,
                             RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findByUserName(user.getUserName());
            if (existingUser != null) {
                redirectAttributes.addFlashAttribute("message", "Username already exists.");
                return "redirect:/register";
            }

            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("message", "User registered successfully!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred.");
            return "redirect:/register";
        }
    }


    @PostMapping("/loginUser")
    //RequestParam bruges til at kræve data fra brugeren - i dette tilfælde brugernavn og kodeord
    //HttpSession bruges til at give brugeren en personlig udgave af app'en, hvor vi efterfølgende kan hente data fra.
    public String loginUser(@RequestParam String userName,
                            @RequestParam String password,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
//Se UserServiceImpl "checkLogin"
        boolean isValidUser = userService.checkLogin(userName, password);
        if (isValidUser) {
            User user = userService.findByUserName(userName);
            session.setAttribute("userId", user.getUserId());
            return "redirect:/items";

        } else {
            redirectAttributes.addFlashAttribute("message", "Incorrect username or password. Try again");
            return "redirect:/login";
        }
    }

//CRUD Item metoder

    @PostMapping("/create")
    public String createItem(@ModelAttribute Item item,
                             HttpSession session,
                            RedirectAttributes redirectAttributes) {
//Henter brugerID fra session.SetAttribute i login-metoden.
//Se Login-metoden
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        item.setUser(user);
        itemService.create(item);
       redirectAttributes.addFlashAttribute("message", "Item added successfully!");
        return "redirect:/items";
    }

    @PostMapping("/delete/{itemId}")
    public String deleteItem(@PathVariable int itemId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Please login to delete items.");
            return "redirect:/login";
        }

        // Kontrollerer om item eksisterer og om den er tilknyttet en user
        Item item = itemService.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getUser().getUserId() != userId) {
            redirectAttributes.addFlashAttribute("message", "Unauthorized to delete this item");
            return "redirect:/items";
        }

        itemService.deleteById(itemId);
        redirectAttributes.addFlashAttribute("message", "Item deleted successfully!");
        return "redirect:/items";
    }

    @PostMapping("/updateItem/{id}")
    public String updateItem(@PathVariable int id,
                             @ModelAttribute Item item,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        itemService.update(id, item);
        redirectAttributes.addFlashAttribute("message", "Item updated successfully!");
        return "redirect:/items";
    }
    @GetMapping("/items")
    public String displayItems(HttpSession session,
                               Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Item> wishlist = itemService.findByUserUserId(userId);
        model.addAttribute("wishlist", wishlist);
        return "items";
    }

}
