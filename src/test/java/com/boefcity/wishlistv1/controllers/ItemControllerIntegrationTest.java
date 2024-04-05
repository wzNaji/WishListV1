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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    public void displayAddForm_ShouldReturnAddFormView() throws Exception {
        mockMvc.perform(get("/addForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("addForm"));
    }

    @Test
    public void saveItem_ShouldRedirectToItems_WhenValid() throws Exception {
        mockMvc.perform(post("/create")
                        .param("name", "NewItem")
                        .param("description", "New Description")
                        .param("link", "http://newitem.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    public void saveItem_ShouldRedirectToErrorPage_WhenInvalid() throws Exception {
        mockMvc.perform(post("/create")
                        .param("name", "") // Missing name to trigger validation
                        .param("description", "No Name")
                        .param("link", "http://noname.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errorPage"));
    }

    @Test
    public void displayItems_ShouldReturnItemsView() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"));
    }

    @Test
    public void editForm_ShouldReturnEditFormView_WhenItemExists() throws Exception {
        Item item = testItem();
        // Use Mockito to define behavior of the mock
        given(itemRepository.findById(any())).willReturn(Optional.of(item));

        mockMvc.perform(get("/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editForm"));
    }

    @Test
    public void updateItem_ShouldRedirectToItems_WhenValid() throws Exception {
        Item item = testItem();
        given(itemRepository.findById(any())).willReturn(Optional.of(item));
        given(itemRepository.save(any(Item.class))).willReturn(item); // Assuming save returns the saved item

        mockMvc.perform(post("/update/1")
                        .param("name", "Updated Item")
                        .param("description", "Updated Description")
                        .param("link", "http://updateditem.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }


    @Test
    public void deleteItem_ShouldRedirectToItems_WhenItemExists() throws Exception {
        given(itemRepository.findById(any())).willReturn(Optional.of(testItem())); // Mock item existence

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
