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
        return "registerForm"; // Name of the HTML file for the registration form
    }
    @GetMapping("/addForm")
    public String displayAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "addForm";
    }
    @GetMapping("/items")
    public String displayItems(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        List<Item> wishlist = itemService.findByUserUserId(userId);
        model.addAttribute("wishlist", wishlist);
        return "items";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
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
    public String loginUser(@RequestParam String userName, @RequestParam String password, HttpSession session) {
        boolean isValidUser = userService.checkLogin(userName, password);
        if (isValidUser) {
            User user = userService.findByUserName(userName);
            session.setAttribute("userId", user.getUserId());
            return "redirect:/items";
        } else {
            return "loginError"; // redirect til samme side, med et redirect flashAtt "no user found"
        }
    }

    @PostMapping("/create")
    public String createItem(@ModelAttribute Item item, HttpSession session, RedirectAttributes redirectAttributes) {
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
    public String deleteItem(@PathVariable int itemId, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "Please login to delete items.");
            return "redirect:/login";
        }

        // Check if the item belongs to the user
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


    // Utility method for session-based wishlist retrieval, adjusted for direct database fetching
    private List<Item> getWishlist(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            return itemService.findByUserUserId(userId);
        }
        return List.of(); // Return an empty list if no user is logged in
    }
}
