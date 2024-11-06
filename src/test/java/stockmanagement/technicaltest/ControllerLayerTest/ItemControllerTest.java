package stockmanagement.technicaltest.ControllerLayerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import stockmanagement.technicaltest.controller.ItemController;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.ItemRepository;
import stockmanagement.technicaltest.service.ItemService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Pen");
        item.setPrice(BigDecimal.valueOf(5));
    }

    @Test
    void testGetItem_Found() throws Exception {
        when(itemService.getItem(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pen"))
                .andExpect(jsonPath("$.price").value(5));
    }

    @Test
    void testGetItem_NotFound() throws Exception {
        when(itemService.getItem(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListItems_Found() throws Exception {
        when(itemService.getItems(any())).thenReturn(Collections.singletonList(item));
        when(itemRepository.count()).thenReturn(1L);

        mockMvc.perform(get("/api/items")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void testListItems_NotFound() throws Exception {
        when(itemService.getItems(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/items")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Items Not Found"));
    }

    @Test
    void testSaveItem_Success() throws Exception {
        when(itemService.saveItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Successfully Save Item!"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testSaveItem_BadRequest() throws Exception {
        item.setName(null); // Trigger validation error

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Name is null"));
    }

    @Test
    void testEditItem_Success() throws Exception {
        when(itemService.saveItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pen"))
                .andExpect(jsonPath("$.price").value(5));
    }

    @Test
    void testDeleteItem_Success() throws Exception {
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }
}

