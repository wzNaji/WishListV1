/*
package com.boefcity.wishlistv1.controllers;


import com.boefcity.wishlistv1.ItemService;
import com.boefcity.wishlistv1.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.boefcity.wishlistv1.TestItem.testItem;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemService itemService;

    @Test
    public void testThatItemIsCreated() throws Exception {
        // Prepare form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id", "someId"); // Assuming ID can be set this way, though typically it's auto-generated and not set in form data
        formData.add("name", "Test Item Name");
        formData.add("description", "Test Description");
        formData.add("link", "http://example.com" );

        mockMvc.perform(MockMvcRequestBuilders.post("/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(formData))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    public void testItemCreationFailsWhenNameIsEmpty() throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        // Omitting name to trigger validation failure
        formData.add("description", "Test Description");
        formData.add("link", "http://example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(formData))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/errorPage"));
    }
}
 */