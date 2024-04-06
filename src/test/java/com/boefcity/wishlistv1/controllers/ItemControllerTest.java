package com.boefcity.wishlistv1.controllers;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;// Use Mockito extension for JUnit to enable mocks and inject mocks functionality
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    // Mock the ItemService to simulate service layer operations without actual implementation
    @Mock
    private ItemService itemService;

    // Mock Spring's Model to simulate adding attributes for view rendering
    @Mock
    private Model model;

    // Automatically inject mocks into the tested object, here ItemController
    @InjectMocks
    private ItemController itemController;

    private Item item; // Declare an item object to be used across different tests

    // Setup method to initialize test data before each test execution
    @BeforeEach
    void setUp() {
        // Initialize the item object with test data
        item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setLink("http://test.com");
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

    // Test for saving a valid item, expecting redirection to the items list
    @Test
    void saveItem_RedirectsToItems() {
        // Execute the save item operation
        String redirectView = itemController.saveItem(item);
        // Assert the expected redirection view name
        assertEquals("redirect:/items", redirectView);
        // Verify that the item service's create method was called exactly once
        verify(itemService, times(1)).create(any(Item.class));
    }

    // Test for attempting to save an invalid item (empty name), expecting redirection to an error page
    @Test
    void saveItem_RedirectsToErrorPage_WhenNameIsEmpty() {
        item.setName(""); // Make the item invalid
        // Perform the operation
        String redirectView = itemController.saveItem(item);
        // Assert redirection to the error page
        assertEquals("redirect:/errorPage", redirectView);
        // Verify the item service's create method was never called due to validation failure
        verify(itemService, never()).create(any(Item.class));
    }

    // Test deleting an existing item, expecting redirection to the items list
    @Test
    void deleteItem_RedirectsToHome_WhenItemExists() {
        // Mock itemService to return an item for given id
        when(itemService.findById(anyInt())).thenReturn(Optional.of(item));
        // Execute delete operation
        String redirectView = itemController.deleteItem(1);
        // Assert redirection to items list
        assertEquals("redirect:/items", redirectView);
        // Verify deletion method called once
        verify(itemService, times(1)).deleteById(1);
    }

    // Test deleting a non-existing item, expecting redirection to an error page
    @Test
    void deleteItem_RedirectsToErrorPage_WhenItemDoesNotExist() {
        // Mock itemService to return empty for given id, simulating not found
        when(itemService.findById(anyInt())).thenReturn(Optional.empty());
        // Perform the delete operation
        String redirectView = itemController.deleteItem(1);
        // Assert redirection to error page
        assertEquals("redirect:/errorPage", redirectView);
        // Verify delete method was never called due to item not existing
        verify(itemService, never()).deleteById(1);
    }

    // Test displaying items, expecting the items view to be returned
    @Test
    void displayItems() {
        // Mock itemService to return a list containing the test item
        when(itemService.findAll()).thenReturn(Arrays.asList(item));
        // Call the method under test
        String viewName = itemController.displayItems(model);
        // Assert that the correct view is returned
        assertEquals("items", viewName);
        // Verify model had 'items' attribute added exactly once
        verify(model, times(1)).addAttribute(eq("items"), anyList());
    }

    // Test editing an existing item, expecting the edit form view
    @Test
    void editForm_RedirectsToEditForm_WhenItemExists() {
        // Mock findById to simulate existing item
        when(itemService.findById(anyInt())).thenReturn(Optional.of(item));
        // Execute the edit form request
        String viewName = itemController.editForm(1, model);
        // Assert the expected view name is returned
        assertEquals("editForm", viewName);
        // Verify model had 'itemDetails' attribute added once
        verify(model, times(1)).addAttribute(eq("itemDetails"), any(Item.class));
    }

    // Test editing a non-existing item, expecting redirection to the error page
    @Test
    void editForm_RedirectsToErrorPage_WhenItemDoesNotExist() {
        // Mock findById to return empty, simulating item not found
        when(itemService.findById(anyInt())).thenReturn(Optional.empty());
        // Attempt to edit a non-existing item
        String viewName = itemController.editForm(1, model);
        // Assert redirection to error page
        assertEquals("errorPage", viewName);
    }

    // Test updating an item, expecting redirection to the items list
    @Test
    void updateItem_RedirectsToItems() {
        // Execute the update operation
        String redirectView = itemController.updateItem(1, item);
        // Assert the expected redirection
        assertEquals("redirect:/items", redirectView);
        // Verify the update method was called once with specific id and item
        verify(itemService, times(1)).update(eq(1), any(Item.class));
    }

    /*
    Uncomment and complete these tests if implementing viewing a single item feature
    @Test
    void viewItem_DisplaysViewItem_WhenItemExists() {
        // Implementation goes here
    }

    @Test
    void viewItem_RedirectsToErrorPage_WhenItemDoesNotExist() {
        // Implementation goes here
    }

     */
}
