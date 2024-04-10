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
        return "loginForm"; // Assume you have a login form at "src/main/resources/templates/loginForm.html"
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String userName, @RequestParam String password, HttpSession session) {
        boolean isValidUser = userService.checkLogin(userName, password);
        if (isValidUser) {
            User user = userService.findByUserName(userName);
            session.setAttribute("userId", user.getUserId());
            return "redirect:/items";
        } else {
            return "loginError"; // Assume you have a login error page at "src/main/resources/templates/loginError.html"
        }
    }

    @GetMapping("/addForm")
    public String displayAddForm(Model model) {
        model.addAttribute("item", new Item());
        return "addForm";
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

    // The rest of your methods (delete, update, etc.) should similarly ensure user is logged in and operate on that user's items

    // Utility method for session-based wishlist retrieval, adjusted for direct database fetching
    private List<Item> getWishlist(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            return itemService.findByUserUserId(userId);
        }
        return List.of(); // Return an empty list if no user is logged in
    }
}
