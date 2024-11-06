package stockmanagement.technicaltest.ServiceLayerTest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.ItemRepository;
import stockmanagement.technicaltest.service.ItemService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup Item entity
        item = new Item();
        item.setId(1L);
        item.setName("Pen");
        item.setStock(100L);
        item.setPrice(BigDecimal.valueOf(5));
    }

    @Test
    void testGetItems() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(itemRepository.findAllWithPagination(pageable)).thenReturn(List.of(item));

        // Act
        var result = itemService.getItems(pageable);

        // Assert
        verify(itemRepository, times(1)).findAllWithPagination(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pen", result.get(0).getName());
    }

    @Test
    void testSaveItem() {
        // Arrange
        when(itemRepository.save(item)).thenReturn(item);

        // Act
        Item result = itemService.saveItem(item);

        // Assert
        verify(itemRepository, times(1)).save(item);
        assertNotNull(result);
        assertEquals("Pen", result.getName());
    }

    @Test
    void testGetItem_Success() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        Optional<Item> result = itemService.getItem(1L);

        // Assert
        verify(itemRepository, times(1)).findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Pen", result.get().getName());
    }

    @Test
    void testGetItem_NotFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Item> result = itemService.getItem(1L);

        // Assert
        verify(itemRepository, times(1)).findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteItem() {
        // Act
        itemService.deleteItem(1L);

        // Assert
        verify(itemRepository, times(1)).deleteById(1L);
    }
}