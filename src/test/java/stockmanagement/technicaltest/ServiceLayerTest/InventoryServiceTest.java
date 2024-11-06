package stockmanagement.technicaltest.ServiceLayerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stockmanagement.technicaltest.model.entity.Inventory;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.InventoryRepository;
import stockmanagement.technicaltest.repository.ItemRepository;
import stockmanagement.technicaltest.service.InventoryService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    private Item item;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup Item entity
        item = new Item();
        item.setId(1L);
        item.setName("Pen");
        item.setStock(10L);

        // Setup Inventory entity
        inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQty(5L);
        inventory.setType("T"); // Assume "T" means adding stock
    }

    @Test
    void testUpdateStock_StockAdded() {
        // Arrange
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.of(item));

        // Act
        inventoryService.updateStock(inventory);

        // Assert
        verify(itemRepository, times(1)).save(item); // Verify that the item was saved
        assertEquals(15L, item.getStock()); // Ensure stock is updated correctly
    }

    @Test
    void testUpdateStock_StockSubtracted() {
        // Change the type to "W" (assuming "W" means subtracting stock)
        inventory.setType("W");

        // Arrange
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.of(item));

        // Act
        inventoryService.updateStock(inventory);

        // Assert
        verify(itemRepository, times(1)).save(item); // Verify that the item was saved
        assertEquals(5L, item.getStock()); // Ensure stock is decreased correctly
    }

    @Test
    void testUpdateStock_ItemNotFound() {
        // Arrange
        when(itemRepository.findById(item.getId())).thenReturn(java.util.Optional.empty()); // Item not found

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> inventoryService.updateStock(inventory));
        assertEquals("Item with name: Pen not found!", exception.getMessage()); // Check the error message
    }
}
