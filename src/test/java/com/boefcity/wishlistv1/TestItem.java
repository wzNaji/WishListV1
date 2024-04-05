package com.boefcity.wishlistv1;

import com.boefcity.wishlistv1.entity.Item;

public final class TestItem {
    public static Item testItem(){
        return new Item(1,"itemName","itemDescription","itemLink");
    }
}
