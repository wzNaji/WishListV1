package com.boefcity.wishlistv1.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.boefcity.wishlistv1.entity.Item;
import com.boefcity.wishlistv1.entity.User;
import com.boefcity.wishlistv1.repository.ItemRepository;
import com.boefcity.wishlistv1.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@SpringBootTest // Indikerer, at denne klasse skal behandles som en Spring Boot-test
@AutoConfigureMockMvc // Aktiverer automatisk konfiguration af MockMvc
@Transactional //Tilføjer annotation til alle metoder - se UserServiceImpl
public class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc; // Mock-objekt til at udføre HTTP-anmodninger

    private MockHttpSession session; // Session-objekt til at simulere en brugers session

    private Item item; // For at bruge 'item' i 'int itemId = item.getId();' testDeleteItemWithSession

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemControllerIT(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        session = new MockHttpSession(); //Instansierer ny session


        User user = new User();
        user.setUserName("testUser");
        user.setUserPassword("testPass");
        user.setItems(new ArrayList<>());

        item = new Item();
        item.setName("itemName");
        item.setDescription("testDescription");
        item.setLink("testLink");
        item.setUser(user);

        user.getItems().add(item);

        userRepository.save(user); // Gemmer kun brugeren, da vi bruger cascadeType.ALL


        session.setAttribute("userId", user.getUserId());
    }
    @AfterEach
    void tearDown() {
        // Ryd alle attributter fra sessionen for at have samme forudsætninger for hver test.
        session.clearAttributes();

        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void testDeleteItemWithSession() throws Exception {
        int itemId = item.getId();
        mockMvc.perform(post("/delete/" + itemId)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    public void testDisplayHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("homePage"));
    }

    @Test
    public void testDisplayRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("registerForm"));
    }

    @Test
    public void testDisplayItemsNoSession() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testDisplayItemsWithSession() throws Exception {
        session.setAttribute("userId", 1);
        mockMvc.perform(get("/items").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("wishlist"))
                .andExpect(view().name("items"));
    }

    @Test
    public void testUpdateItemNoSession() throws Exception {
        mockMvc.perform(post("/updateItem/1")
                        .param("id", "1")
                        .flashAttr("item", new Item()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testUpdateItemWithSession() throws Exception {
        Integer userId = (Integer) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getItems().isEmpty()) {
            throw new RuntimeException("No items found for the user");
        }
        Item item = user.getItems().get(0); // Første item object i user's items liste.

        mockMvc.perform(post("/updateItem/" + item.getId())
                        .session(session)
                        .flashAttr("item", item))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }



    @Test
    public void testCreateUserWithExistingUsername() throws Exception {
        User user = new User();
        user.setUserName("testUser"); //Setter navnet på den nye bruger til samme brugernavn som i vores BeforeEach
        user.setUserPassword("testPass");
        mockMvc.perform(post("/register")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"));
    }

    @Test
    public void testCreateUserNew() throws Exception {
        User user = new User(2, "newTestUser", "testPass", new ArrayList<>());
        mockMvc.perform(post("/register")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testLoginUserInvalidCredentials() throws Exception {
        mockMvc.perform(post("/loginUser")
                        .param("userName", "user")
                        .param("password", "wrongPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Incorrect username or password. Try again"));
    }

    @Test
    public void testLoginUserValidCredentials() throws Exception {
        mockMvc.perform(post("/loginUser")
                        .param("userName", "testUser")
                        .param("password", "testPass")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    public void testDeleteItemNoSession() throws Exception {
        mockMvc.perform(post("/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

}
