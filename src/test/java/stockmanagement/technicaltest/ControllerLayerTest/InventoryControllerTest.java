package stockmanagement.technicaltest.ControllerLayerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import stockmanagement.technicaltest.controller.InventoryController;
import stockmanagement.technicaltest.model.entity.Inventory;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.InventoryRepository;
import stockmanagement.technicaltest.service.InventoryService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private InventoryRepository inventoryRepository;

    private Inventory inventory;
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setStock(1L);
        item.setName("Pen");
        item.setPrice(BigDecimal.valueOf(5));

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setItem(item);
        inventory.setQty(10L);
        inventory.setType("T");
    }

    @Test
    void testGetInventory_Found() throws Exception {
        when(inventoryService.getInventory(1L)).thenReturn(Optional.of(inventory));

        mockMvc.perform(get("/api/inventories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Pen"))
                .andExpect(jsonPath("$.qty").value(10))
                .andExpect(jsonPath("$.type").value("T"));
    }

    @Test
    void testGetInventory_NotFound() throws Exception {
        when(inventoryService.getInventory(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/inventories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveInventory_Success() throws Exception {
        when(inventoryService.saveInventory(any(Inventory.class))).thenReturn(inventory);

        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(inventory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Successfully Save Inventories!"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testSaveInventory_InvalidData() throws Exception {
        inventory.setQty(-1L); // Set invalid quantity

        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(inventory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("Quantity must be greater than zero"));
    }

    @Test
    void testEditInventory_Success() throws Exception {
        when(inventoryService.saveInventory(any(Inventory.class))).thenReturn(inventory);

        mockMvc.perform(put("/api/inventories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(inventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Pen"))
                .andExpect(jsonPath("$.qty").value(10))
                .andExpect(jsonPath("$.type").value("T"));
    }

    @Test
    void testDeleteInventory_Success() throws Exception {
        doNothing().when(inventoryService).deleteInventory(1L);

        mockMvc.perform(delete("/api/inventories/1"))
                .andExpect(status().isNoContent());
    }
}
