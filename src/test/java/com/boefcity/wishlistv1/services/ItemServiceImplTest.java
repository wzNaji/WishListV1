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

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

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
    void whenCreateItem_thenItemIsSaved() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        itemService.create(item);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void whenValidId_thenItemShouldBeFound() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Optional<Item> foundItem = itemService.findById(1);
        assert foundItem.isPresent() : "Item should be found";
        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    void whenDeleteItem_thenItemShouldBeDeleted() {
        doNothing().when(itemRepository).deleteById(1);
        itemService.deleteById(1);
        verify(itemRepository, times(1)).deleteById(1);
    }

    @Test
    void whenFindAllItems_thenItemsShouldBeReturned() {
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);
        List<Item> returnedItems = itemService.findAll();
        assert returnedItems.size() == 1 : "One item should be returned";
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void whenUpdateItem_thenItemShouldBeUpdated() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item updatedItem = new Item();
        updatedItem.setName("Updated Name");
        updatedItem.setDescription("Updated Description");
        updatedItem.setLink("http://updated.com");

        Item result = itemService.update(1, updatedItem);

        assert "Updated Name".equals(result.getName()) : "Item name should be updated";
        verify(itemRepository).save(item);
        verify(itemRepository).findById(1);
    }
}
