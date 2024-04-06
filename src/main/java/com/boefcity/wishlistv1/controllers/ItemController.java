package com.boefcity.wishlistv1.controllers;

// Import statements bring in required classes from external libraries and other parts of the application.
import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @Controller annotation indicates that this class is a Spring MVC controller.
@Controller
public class ItemController {
    // Dependency injection of ItemService to interact with item data (e.g., database operations).
    private final ItemService itemService;

    // Constructor for the controller, Spring will inject an instance of ItemService at runtime.
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // @GetMapping annotation handles HTTP GET requests. This method displays the form to add a new item.
    @GetMapping("/addForm")
    public String displayAddForm(Model model) {
        // Adds a new, empty Item object to the model so the form can bind to it.
        model.addAttribute("item", new Item());
        // Returns the name of the template to render, in this case, "addForm".
        return "addForm";
    }

    // @PostMapping handles HTTP POST requests. This method processes the submitted form for creating a new item.
    @PostMapping("/create")
    public String saveItem(@ModelAttribute Item item, HttpSession session) {
        // Checks if the item's name is empty and redirects to an error page if true.
        if (item.getName().isEmpty()) {
            return "redirect:/errorPage";
        }
        // Retrieves the current wishlist from the session, creating a new one if it doesn't exist.
        List<Item> wishlist = getWishlist(session);
        // Adds the new item to the wishlist.
        wishlist.add(item);
        // Updates the session with the new wishlist.
        session.setAttribute("wishlist", wishlist);
        // Redirects to the "/items" page, which will display the updated wishlist.
        return "redirect:/items";
    }

    // Handles the deletion of an item from the wishlist.
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable int id, HttpSession session) {
        // Retrieves the wishlist from the session.
        List<Item> wishlist = getWishlist(session);
        // Removes the item with the given id from the wishlist.
        wishlist.removeIf(item -> item.getId() == id);
        // Updates the session with the modified wishlist.
        session.setAttribute("wishlist", wishlist);
        // Redirects to the "/items" page.
        return "redirect:/items";
    }

    // Displays all items in the wishlist.
    @GetMapping("/items")
    public String displayItems(HttpSession session, Model model) {
        // Retrieves the wishlist from the session.
        List<Item> wishlist = getWishlist(session);
        // Adds the wishlist to the model to be displayed in the view.
        model.addAttribute("items", wishlist);
        // Returns the name of the view template to render the wishlist.
        return "items";
    }

    // Displays the form for editing an existing item.
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model, HttpSession session) {
        // Retrieves the wishlist and finds the item to be edited.
        List<Item> wishlist = getWishlist(session);
        Optional<Item> itemToEdit = wishlist.stream().filter(item -> item.getId() == id).findFirst();
        // If the item is found, adds it to the model; otherwise, redirects to an error page.
        itemToEdit.ifPresent(item -> model.addAttribute("itemDetails", item));
        return itemToEdit.isPresent() ? "editForm" : "redirect:/errorPage";
    }

    // Processes the form submission for updating an item.
    @PostMapping("/update/{id}")
    public String updateItem(@PathVariable int id, @ModelAttribute("itemDetails") Item itemDetails, HttpSession session) {
        // Retrieves the wishlist and replaces the item with updated details.
        List<Item> wishlist = getWishlist(session);
        wishlist.replaceAll(item -> item.getId() == id ? itemDetails : item);
        // Updates the session with the modified wishlist.
        session.setAttribute("wishlist", wishlist);
        // Redirects to the "/items" page.
        return "redirect:/items";
    }

    // Saves the current wishlist to the database (or another storage medium) and clears it from the session.
    @GetMapping("/save-wishlist")
    public String saveWishlist(HttpSession session) {
        // Retrieves the wishlist from the session.
        List<Item> wishlist = getWishlist(session);
        // Iterates over the wishlist and saves each item using the itemService.
        for (Item item : wishlist) {
            itemService.create(new Item(item.getId(), item.getName(), item.getDescription(), item.getLink()));
        }
        // Clears the wishlist from the session.
        session.removeAttribute("wishlist");
        // Redirects to the "/items" page.
        return "redirect:/items";
    }

    // Utility method to safely retrieve or create the wishlist from the session.
    private List<Item> getWishlist(HttpSession session) {
        List<Item> wishlist = (List<Item>) session.getAttribute("wishlist");
        if (wishlist == null) {
            wishlist = new ArrayList<>();
        }
        return wishlist;
    }
}
