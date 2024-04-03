package com.boefcity.wishlistv1;

import com.boefcity.wishlistv1.entity.Item;

import java.util.Optional;

public interface ItemService {
    Item create (Item item);
    Optional<Item> findById (int id);

    void deleteById(int id);
}
