package com.boefcity.wishlistv1;

import com.boefcity.wishlistv1.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    void create (Item item);
    Optional<Item> findById (int id);

    void deleteById(int id);

    List<Item> findAll();

    void update(int id);
}
