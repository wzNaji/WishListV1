package com.boefcity.wishlistv1.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.UserService;
import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.entity.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
public class ItemControllerUnitTest {

    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private HttpSession session;

    @InjectMocks
    private ItemController controller;


    @Test
    public void testDisplayHomePage() {
        String viewName = controller.displayHomePage();
        assertEquals("homePage", viewName);
    }

    @Test
    public void testDisplayLoginForm() {
        String viewName = controller.displayLoginForm();
        assertEquals("loginForm", viewName);
    }

    @Test
    public void testDisplayRegisterForm() {
        when(model.addAttribute(eq("user"), any(User.class))).thenReturn(model);
        String viewName = controller.displayRegisterForm(model);
        assertEquals("registerForm", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    // Create
    @Test
    public void testCreateUserAlreadyExists() {
        User user = new User();
        user.setUserName("existingUser");
        when(userService.findByUserName("existingUser")).thenReturn(user);

        String result = controller.createUser(user, redirectAttributes);
        assertEquals("redirect:/register", result);
        verify(redirectAttributes).addFlashAttribute("message", "Username already exists.");
    }

    @Test
    public void testCreateUserSuccess() {
        User newUser = new User();
        newUser.setUserName("newUser");
        when(userService.findByUserName("newUser")).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(newUser);

        String result = controller.createUser(newUser, redirectAttributes);
        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("message", "User registered successfully!");
    }
    @Test
    public void testDeleteItemNoUserSession() {
        when(session.getAttribute("userId")).thenReturn(null);

        String result = controller.deleteItem(1, session, redirectAttributes);
        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("message", "Please login to delete items.");
    }

    // Delete
    @Test
    public void testDeleteItemWithSessionButUnauthorized() {
        User user = new User(2, "AnotherUser"); // Assuming the constructor takes userId and userName
        Item item = new Item();
        item.setUser(user);
        when(session.getAttribute("userId")).thenReturn(1);
        when(itemService.findById(1)).thenReturn(java.util.Optional.of(item));

        String result = controller.deleteItem(1, session, redirectAttributes);
        assertEquals("redirect:/items", result);
        verify(redirectAttributes).addFlashAttribute("message", "Unauthorized to delete this item");
    }

    @Test
    public void testDeleteItemAuthorized() {
        User user = new User(1, "CurrentUser");
        Item item = new Item();
        item.setUser(user);
        when(session.getAttribute("userId")).thenReturn(1);
        when(itemService.findById(1)).thenReturn(java.util.Optional.of(item));
        doNothing().when(itemService).deleteById(1);

        String result = controller.deleteItem(1, session, redirectAttributes);
        assertEquals("redirect:/items", result);
        verify(redirectAttributes).addFlashAttribute("message", "Item deleted successfully!");
    }

    // Update
    @Test
    public void testUpdateItemNotLoggedIn() {
        when(session.getAttribute("userId")).thenReturn(null);

        String result = controller.updateItem(1, new Item(), session, redirectAttributes);
        assertEquals("redirect:/login", result);
    }

    @Test
    public void testUpdateItemSuccess() {
        Integer userId = 1;
        Item item = new Item();
        when(session.getAttribute("userId")).thenReturn(userId);
        when(itemService.update(eq(1), any(Item.class))).thenReturn(item);

        String result = controller.updateItem(1, item, session, redirectAttributes);
        assertEquals("redirect:/items", result);
        verify(redirectAttributes).addFlashAttribute("message", "Item updated successfully!");
    }


}
