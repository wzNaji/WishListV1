package com.boefcity.wishlistv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// Se Item class for annotations comments
@Data
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {

    @Id // Markerer userId som PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genererer automatisk et unikt id for hver bruger
    private int userId;

    // userName kan ikke være null
    @Column(nullable = false, unique = true)
    private String userName;

    // Se username
    @Column(nullable = false)
    private String userPassword;

    //  Liste over items tilknyttet useren med cascade sletning og fjernelse af child elements
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    public User(int userId, String userName, String userPassword, List<Item> items) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.items = new ArrayList<>(items);
    }

    // Custom constructor brugt i tests
    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
    @Override
    public String toString() { // uden password
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", items=" + (items != null ? items.size() : 0) + " items" +
                '}';
    }

}
