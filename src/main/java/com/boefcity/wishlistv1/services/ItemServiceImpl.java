package com.boefcity.wishlistv1.services;

import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional(readOnly = true) // Når vi kun skal læse fra databasen. Optimerer resource usage og perfomance.
    @Override
    public Optional<Item> findById(int id) {
        return itemRepository.findById(id);

    }

    @Override
    public void deleteById(int id) {
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true) // Når vi kun skal læse fra databasen. Optimerer resource usage og perfomance.
    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }


    @Transactional
    @Override
    public Item update(int id, Item itemDetails) {
        Item itemToUpdate = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found for this id :: " + id));

        itemToUpdate.setName(itemDetails.getName());
        itemToUpdate.setDescription(itemDetails.getDescription());
        itemToUpdate.setLink(itemDetails.getLink());

        return itemRepository.save(itemToUpdate);
    }

}


