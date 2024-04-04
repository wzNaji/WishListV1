package com.boefcity.wishlistv1.services;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void create(Item item) {
        itemRepository.save(item);
    }

    @Override
    public Optional<Item> findById(int id) {
        return itemRepository.findById(id);

    }

    @Override
    public void deleteById(int id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional
    public Item updateItem(int id, Item itemDetails) {
        Item itemToUpdate = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found for this id :: " + id));

        itemToUpdate.setName(itemDetails.getName());
        itemToUpdate.setDescription(itemDetails.getDescription());
        itemToUpdate.setLink(itemDetails.getLink());

        return itemRepository.save(itemToUpdate);
    }


}
