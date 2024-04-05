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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private Model model;

    @InjectMocks
    private ItemController itemController;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setLink("http://test.com");
    }

    @Test
    void displayAddForm() {
        String viewName = itemController.displayAddForm(model);
        assertEquals("addForm", viewName);
        verify(model, times(1)).addAttribute(eq("item"), any(Item.class));
    }

    @Test
    void saveItem_RedirectsToItems() {
        String redirectView = itemController.saveItem(item);
        assertEquals("redirect:/items", redirectView);
        verify(itemService, times(1)).create(any(Item.class));
    }

    @Test
    void saveItem_RedirectsToErrorPage_WhenNameIsEmpty() {
        item.setName(""); // Make the item name empty to trigger the redirection to the error page
        String redirectView = itemController.saveItem(item);
        assertEquals("redirect:/errorPage", redirectView);
        verify(itemService, never()).create(any(Item.class));
    }

    @Test
    void deleteItem_RedirectsToHome_WhenItemExists() {
        when(itemService.findById(anyInt())).thenReturn(Optional.of(item));
        String redirectView = itemController.deleteItem(1);
        assertEquals("redirect:/items", redirectView);
        verify(itemService, times(1)).deleteById(1);
    }

    @Test
    void deleteItem_RedirectsToErrorPage_WhenItemDoesNotExist() {
        when(itemService.findById(anyInt())).thenReturn(Optional.empty());
        String redirectView = itemController.deleteItem(1);
        assertEquals("redirect:/errorPage", redirectView);
        verify(itemService, never()).deleteById(1);
    }

    @Test
    void displayItems() {
        when(itemService.findAll()).thenReturn(Arrays.asList(item));
        String viewName = itemController.displayItems(model);
        assertEquals("items", viewName);
        verify(model, times(1)).addAttribute(eq("items"), anyList());
    }

    @Test
    void editForm_RedirectsToEditForm_WhenItemExists() {
        when(itemService.findById(anyInt())).thenReturn(Optional.of(item));
        String viewName = itemController.editForm(1, model);
        assertEquals("editForm", viewName);
        verify(model, times(1)).addAttribute(eq("itemDetails"), any(Item.class));
    }

    @Test
    void editForm_RedirectsToErrorPage_WhenItemDoesNotExist() {
        when(itemService.findById(anyInt())).thenReturn(Optional.empty());
        String viewName = itemController.editForm(1, model);
        assertEquals("errorPage", viewName);
    }

    @Test
    void updateItem_RedirectsToItems() {
        String redirectView = itemController.updateItem(1, item);
        assertEquals("redirect:/items", redirectView);
        verify(itemService, times(1)).update(eq(1), any(Item.class));
    }

    /*
    LAV 'viewItem' TEMPLATE HVIS DENNE SKAL I BRUG
    @Test
    void viewItem_DisplaysViewItem_WhenItemExists() {
        when(itemService.findById(anyInt())).thenReturn(Optional.of(item));
        String viewName = itemController.viewItem(1, model);
        assertEquals("viewItem", viewName);
        verify(model, times(1)).addAttribute(eq("item"), any(Item.class));
    }

    @Test
    void viewItem_RedirectsToErrorPage_WhenItemDoesNotExist() {
        when(itemService.findById(anyInt())).thenReturn(Optional.empty());
        String viewName = itemController.viewItem(1, model);
        assertEquals("redirect:/errorPage", viewName);
    }

     */
}
