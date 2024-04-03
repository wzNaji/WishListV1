package com.boefcity.wishlistv1.repository;

import com.boefcity.wishlistv1.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {



}
