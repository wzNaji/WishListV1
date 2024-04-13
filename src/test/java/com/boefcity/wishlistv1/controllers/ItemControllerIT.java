package com.boefcity.wishlistv1.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.UserService;
import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(ItemController.class)
public class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @Test
    public void testDisplayHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePage"));
    }

    @Test
    public void testDisplayRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("registerForm"));
    }

    // Display items with & without session
    @Test
    public void testDisplayItemsNoSession() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testDisplayItemsWithSession() throws Exception {
        session.setAttribute("userId", 1);
        when(itemService.findByUserUserId(1)).thenReturn(List.of(new Item())); // assuming Item is properly defined
        mockMvc.perform(get("/items").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("wishlist"))
                .andExpect(view().name("items"));
    }

    // Update functionality
    @Test
    public void testUpdateItemNoSession() throws Exception {
        mockMvc.perform(post("/updateItem/1")
                        .param("id", "1")
                        .flashAttr("item", new Item()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testUpdateItemWithSession() throws Exception {
        session.setAttribute("userId", 1);
        Item item = new Item();
        when(itemService.update(anyInt(), any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/updateItem/1")
                        .session(session)
                        .flashAttr("item", item))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    // Create new user
    @Test
    public void testCreateUserWithExistingUsername() throws Exception {
        User user = new User();
        user.setUserName("existing");
        when(userService.findByUserName("existing")).thenReturn(user);

        mockMvc.perform(post("/register")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"));
    }

    @Test
    public void testCreateUserNew() throws Exception {
        User user = new User();
        user.setUserName("newUser");
        when(userService.findByUserName("newUser")).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(user); // Adjust based on actual return type

        mockMvc.perform(post("/register")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // User login
    @Test
    public void testLoginUserInvalidCredentials() throws Exception {
        when(userService.checkLogin("user", "wrongPass")).thenReturn(false);

        mockMvc.perform(post("/loginUser")
                        .param("userName", "user")
                        .param("password", "wrongPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Incorrect username or password. Try again"));
    }
    @Test
    public void testLoginUserValidCredentials() throws Exception {
        when(userService.checkLogin("validUser", "password")).thenReturn(true);
        User user = new User();
        user.setUserId(1);
        when(userService.findByUserName("validUser")).thenReturn(user);

        mockMvc.perform(post("/loginUser")
                        .param("userName", "validUser")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    // Delete functionality
    @Test
    public void testDeleteItemNoSession() throws Exception {
        mockMvc.perform(post("/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testDeleteItemWithSession() throws Exception {
        session.setAttribute("userId", 1);
        Item item = new Item();
        item.setUser(new User(1, "User")); // User constructor with id and username

        when(itemService.findById(1)).thenReturn(java.util.Optional.of(item));
        doNothing().when(itemService).deleteById(1);

        mockMvc.perform(post("/delete/1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }






}
