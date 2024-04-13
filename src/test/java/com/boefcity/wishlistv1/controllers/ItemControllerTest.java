package com.boefcity.wishlistv1.controllers;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;// Use Mockito extension for JUnit to enable mocks and inject mocks functionality
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    // Mock the ItemService to simulate service layer operations without actual implementation
    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    // Mock Spring's Model to simulate adding attributes for view rendering
    @Mock
    private Model model;

    // Automatically inject mocks into the tested object, here ItemController
    @InjectMocks
    private ItemController itemController;

    private Item item;

    private final MockHttpSession session = new MockHttpSession();

    // Setup method to initialize test data before each test execution
    @BeforeEach
    void setUp() {
        // Initialize the item object with test data
        item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setLink("http://test.com");

        // Assuming you have a mocked userService and itemService in your test setup
        // Prepare user and session setup for authenticated scenarios
        User user = new User();
        user.setUserId(1); // Ensure this ID aligns with your test cases

        // Mock the userService to return a user when looked up by ID or username, if needed
        when(userService.findById(1)).thenReturn(Optional.of(user));
        when(userService.findByUserName("existingUser")).thenReturn(user);

        // Prepare the session to simulate a user being logged in
        session.setAttribute("userId", user.getUserId());

        // Prepare the itemService to return an item when findById is called
        when(itemService.findById(1)).thenReturn(Optional.of(item));
    }

    // Test for displaying the add form with an empty Item model
    @Test
    void displayAddForm() {
        // Call the method under test
        String viewName = itemController.displayAddForm(model);
        // Assert the expected view name
        assertEquals("addForm", viewName);
        // Verify the model had an 'item' attribute added exactly once
        verify(model, times(1)).addAttribute(eq("item"), any(Item.class));
    }

    @Test
    void createItem_RedirectsToItems() {
        // Mock redirection attributes
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Simulate a logged-in user
        session.setAttribute("userId", 1);
        when(userService.findById(1)).thenReturn(Optional.of(new User()));

        // Setup item to be created
        Item newItem = new Item();
        newItem.setName("New Item");

        // Execute the create item operation
        String redirectView = itemController.createItem(newItem, session, redirectAttributes);

        // Assert the expected redirection view name
        assertEquals("redirect:/items", redirectView);

        // Verify that the itemService.create method was called
        verify(itemService).create(any(Item.class));

        // Check redirection attributes
        verify(redirectAttributes).addFlashAttribute("message", "Item added successfully!");
    }
    @Test
    void updateItem_RedirectsToItems_WithValidUserAndItem() {
        // Mock redirection attributes
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String redirectView = itemController.updateItem(item.getId(), item, session, redirectAttributes);

        // Assert
        assertEquals("redirect:/items", redirectView);
        verify(itemService).findById(1); // Verify that findById was called
        verify(itemService).update(eq(1), any(Item.class)); // Verify that update was called
        verify(redirectAttributes).addFlashAttribute("message", "Item updated successfully!");
    }




}
