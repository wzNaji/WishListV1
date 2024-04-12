package com.boefcity.wishlistv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genererer boilerplate (getters, setters..)
@NoArgsConstructor // Genererer en konstruktør uden argumenter
@AllArgsConstructor // Genererer en konstruktør med alle argumenter
@Entity // Enhed i JPA-kontekst
@Table(name="items") // Mapper items entity/klasse til en tabel hvor name = "items" i databasen
public class Item {
    @Id //Angiver primærnøglen
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID/PK
    private int id;

    @Column(nullable = false) // Gør kolonnen non-nullable
    private String name;

    private String description;
    private String link;

    @ManyToOne // Etablerer et Many-To-One forhold til User entity
    @JoinColumn(name = "user_id") // Mapper til 'user_id' kolonnen i databasen
    private User user; // Repræsenterer brugeren, der er forbundet med denne vare
}
