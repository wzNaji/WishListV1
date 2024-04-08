package com.boefcity.wishlistv1.controllers;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
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
        // Mock HttpSession
        HttpSession session = mock(HttpSession.class);
        List<Item> wishlist = new ArrayList<>();
        when(session.getAttribute("wishlist")).thenReturn(wishlist);

        // Execute the save item operation
        Item item = new Item();
        item.setName("Test Item");
        String redirectView = itemController.saveItem(item, session);

        // Assert the expected redirection view name
        assertEquals("redirect:/items", redirectView);

        // Verify the wishlist in the session is updated
        assertEquals(1, wishlist.size()); // Ensure the wishlist now contains the item
        verify(session, times(1)).setAttribute(eq("wishlist"), any());
    }


    // Test for attempting to save an invalid item (empty name), expecting redirection to an error page
    @Test
    void saveItem_RedirectsToErrorPage_WhenNameIsEmpty() {
        // Mock HttpSession
        HttpSession session = mock(HttpSession.class);

        // Prepare the item with an empty name
        Item item = new Item();
        item.setName("");

        // Perform the operation
        String redirectView = itemController.saveItem(item, session);

        // Assert redirection to the error page
        assertEquals("redirect:/errorPage", redirectView);

        // Since the operation should fail before interacting with the session,
        // no interactions with the session to set the attribute are expected
        verify(session, never()).setAttribute(eq("wishlist"), any());
    }


    @Test
    void saveItem_RedirectsToItems_WhenValid() {
        // Given an item with a non-empty name
        String redirectView = itemController.saveItem(item, session);

        // Expect redirection to the items list
        assertEquals("redirect:/items", redirectView);

        // And the session contains the wishlist with the item
        @SuppressWarnings("unchecked")
        List<Item> wishlist = (List<Item>) session.getAttribute("wishlist");
        assertEquals(1, wishlist.size());
        assertEquals(item.getName(), wishlist.get(0).getName());
    }

    @Test
    void deleteItem_RedirectsToItems_WhenItemExists() {
        // Given a wishlist with one item
        List<Item> wishlist = new ArrayList<>();
        wishlist.add(item);
        session.setAttribute("wishlist", wishlist);

        // When deleting an existing item
        String redirectView = itemController.deleteItem(item.getId(), session);

        // Expect redirection to the items list
        assertEquals("redirect:/items", redirectView);

        // And the session wishlist is empty
        wishlist = (List<Item>) session.getAttribute("wishlist");
        assertTrue(wishlist.isEmpty());
    }

    @Test
    void displayItems_ShowsItemsInView() {
        // Given a wishlist in the session
        List<Item> wishlist = new ArrayList<>();
        wishlist.add(item);
        session.setAttribute("wishlist", wishlist);

        Model model = new ExtendedModelMap();

        // When displaying items
        String viewName = itemController.displayItems(session, model);

        // Expect the items view is returned
        assertEquals("items", viewName);

        // And the model contains the wishlist from the session
        assertEquals(wishlist, model.getAttribute("items"));
    }



// Your existing test class code...

    @Test
    void saveWishlist_SavesItemsToDatabaseAndRedirects() {
        // Given a wishlist in the session
        List<Item> wishlist = new ArrayList<>();
        wishlist.add(item);
        session.setAttribute("wishlist", wishlist);

        // Mock RedirectAttributes
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // When saving the wishlist to the database
        String redirectView = itemController.saveWishlist(session, redirectAttributes);

        // Expect redirection to the items list
        assertEquals("redirect:/items", redirectView);

        // And the wishlist is saved using itemService
        verify(itemService, times(1)).create(any(Item.class));

        // And the session wishlist is cleared
        assertNull(session.getAttribute("wishlist"));

        // Optionally verify that the success message was added to redirect attributes
        assertNotNull(redirectAttributes.getFlashAttributes().get("successMessage"));
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
