package com.boefcity.wishlistv1.services;

import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;// Use Mockito testing tools with JUnit 5 tests
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    // Create a mock version of ItemRepository to simulate database operations
    @Mock
    private ItemRepository itemRepository;

    // Inject the mock ItemRepository into an actual ItemServiceImpl instance
    @InjectMocks
    private ItemServiceImpl itemService;

    // Declare a common Item object to be used in the tests
    private Item item;

    // Set up common test objects before each test method runs
    @BeforeEach
    void setUp() {
        // Initialize the Item object with test data
        item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setLink("http://test.com");
    }

    // Test the create functionality of the ItemService
    @Test
    void whenCreateItem_thenItemIsSaved() {
        // Configure the mock repository to return the test item when save is called
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        // Call the create method, which should save the item
        itemService.create(item);
        // Verify that save was called exactly once with the test item
        verify(itemRepository, times(1)).save(item);
    }

    // Test the findById functionality of the ItemService
    @Test
    void whenValidId_thenItemShouldBeFound() {
        // Configure the mock repository to return an Optional of the test item when findById is called
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        // Attempt to find the item by its ID
        Optional<Item> foundItem = itemService.findById(1);
        // Assert that an item was found
        assert foundItem.isPresent() : "Item should be found";
        // Verify that findById was called exactly once with ID 1
        verify(itemRepository, times(1)).findById(1);
    }

    // Test the deleteById functionality of the ItemService
    @Test
    void whenDeleteItem_thenItemShouldBeDeleted() {
        // Configure the mock repository to do nothing when deleteById is called
        doNothing().when(itemRepository).deleteById(1);
        // Call deleteById method, which should delete the item
        itemService.deleteById(1);
        // Verify that deleteById was called exactly once with ID 1
        verify(itemRepository, times(1)).deleteById(1);
    }

    // Test the findAll functionality of the ItemService
    @Test
    void whenFindAllItems_thenItemsShouldBeReturned() {
        // Configure the mock repository to return a list containing the test item when findAll is called
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);
        // Call the findAll method, which should return a list of items
        List<Item> returnedItems = itemService.findAll();
        // Assert that one item is returned
        assert returnedItems.size() == 1 : "One item should be returned";
        // Verify that findAll was called exactly once
        verify(itemRepository, times(1)).findAll();
    }

    // Test the update functionality of the ItemService
    @Test
    void whenUpdateItem_thenItemShouldBeUpdated() {
        // Configure the mock repository to return the test item when findById is called
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        // Configure the mock to return the test item when save is called
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // Create an updated item object
        Item updatedItem = new Item();
        updatedItem.setName("Updated Name");
        updatedItem.setDescription("Updated Description");
        updatedItem.setLink("http://updated.com");

        // Call the update method, which should update the item
        Item result = itemService.update(1, updatedItem);

        // Assert that the item's name was updated
        assert "Updated Name".equals(result.getName()) : "Item name should be updated";
        // Verify that save and findById were called as expected
        verify(itemRepository).save(item);
        verify(itemRepository).findById(1);
    }
}
