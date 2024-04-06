package com.boefcity.wishlistv1.controllers;

import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.boefcity.wishlistv1.TestItem.testItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// Define a class to perform integration tests on the ItemController
@SpringBootTest // Indicates that the ApplicationContext should be loaded for integration tests
@AutoConfigureMockMvc // Automatically configure MockMvc for testing Spring MVC controllers without starting the server
@ActiveProfiles("test") // Specify the active profile as 'test' to use application-test.properties configurations
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Inject the MockMvc instance to simulate HTTP requests

    @MockBean
    private ItemRepository itemRepository; // Mock the ItemRepository to not directly interact with the database

    // Test case for displaying the add form view
    @Test
    public void displayAddForm_ShouldReturnAddFormView() throws Exception {
        // Perform a GET request to "/addForm" and expect the view name to be "addForm" with HTTP status 200 OK
        mockMvc.perform(get("/addForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("addForm"));
    }

    // Test case for saving a valid item and redirecting to the items listing
    @Test
    public void saveItem_ShouldRedirectToItems_WhenValid() throws Exception {
        // Perform a POST request to "/create" with item parameters and expect a redirection to "/items"
        mockMvc.perform(post("/create")
                        .param("name", "NewItem")
                        .param("description", "New Description")
                        .param("link", "http://newitem.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    // Test case for attempting to save an invalid item and redirecting to an error page
    @Test
    public void saveItem_ShouldRedirectToErrorPage_WhenInvalid() throws Exception {
        // Perform a POST request to "/create" with invalid parameters (e.g., empty name) and expect redirection to "/errorPage"
        mockMvc.perform(post("/create")
                        .param("name", "") // Invalid due to missing name
                        .param("description", "No Name")
                        .param("link", "http://noname.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errorPage"));
    }

    // Test case for displaying all items
    @Test
    public void displayItems_ShouldReturnItemsView() throws Exception {
        // Perform a GET request to "/items" and expect the view name to be "items" with HTTP status 200 OK
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"));
    }

    // Test case for displaying the edit form for an existing item
    @Test
    public void editForm_ShouldReturnEditFormView_WhenItemExists() throws Exception {
        Item item = testItem(); // Create a test item
        // Mock the findById method to return the test item
        given(itemRepository.findById(any())).willReturn(Optional.of(item));

        // Perform a GET request to "/edit/1" and expect the view name to be "editForm"
        mockMvc.perform(get("/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editForm"));
    }

    // Test case for updating a valid item and redirecting to the items listing
    @Test
    public void updateItem_ShouldRedirectToItems_WhenValid() throws Exception {
        Item item = testItem(); // Create a test item
        // Mock the findById and save methods to return the test item
        given(itemRepository.findById(any())).willReturn(Optional.of(item));
        given(itemRepository.save(any(Item.class))).willReturn(item);

        // Perform a POST request to "/update/1" with updated item parameters and expect a redirection to "/items"
        mockMvc.perform(post("/update/1")
                        .param("name", "Updated Item")
                        .param("description", "Updated Description")
                        .param("link", "http://updateditem.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    // Test case for deleting an existing item and redirecting to the items listing
    @Test
    public void deleteItem_ShouldRedirectToItems_WhenItemExists() throws Exception {
        // Mock the findById method to simulate the existence of an item to be deleted
        given(itemRepository.findById(any())).willReturn(Optional.of(testItem()));

        // Perform a GET request to "/delete/1" and expect a redirection to "/items"
        mockMvc.perform(get("/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }
    /*
    LAV EN 'viewItem' TEMPLATE HVIS DENNE SKAL I BRUG
    @Test
    public void viewItem_ShouldReturnViewItemView_WhenItemExists() throws Exception {
        given(itemRepository.findById(any())).willReturn(Optional.of(testItem())); // Mock item existence

        mockMvc.perform(get("/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"));
    }

     */

}
