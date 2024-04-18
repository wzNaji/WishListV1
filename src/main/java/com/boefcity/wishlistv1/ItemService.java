package com.boefcity.wishlistv1;

import com.boefcity.wishlistv1.entity.Item;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface ItemService {
    //ItemService bruges til seperation of concern og loose coupling osv.
    void create (Item item);
    Optional<Item> findById (int id);

    void deleteById(int id);

    List<Item> findAll();

    Item update(int id, Item itemDetails);
    List<Item> findByUserUserId(int userId);
}
