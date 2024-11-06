package stockmanagement.technicaltest.ServiceLayerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.model.entity.Orders;
import stockmanagement.technicaltest.repository.ItemRepository;
import stockmanagement.technicaltest.repository.OrdersRepository;
import stockmanagement.technicaltest.service.OrdersService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrdersServiceTest {

    @InjectMocks
    private OrdersService ordersService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ItemRepository itemRepository;

    private Orders order;
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

        // Setup Orders entity
        order = new Orders();
        order.setId(1L);
        order.setItem(item);
        order.setQty(10L);
        order.setPrice(BigDecimal.valueOf(50));
    }

    @Test
    void testGetOrder_Success() {
        // Arrange
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Orders result = ordersService.getOrder(1L);

        // Assert
        verify(ordersRepository, times(1)).findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pen", result.getItem().getName());
    }

    @Test
    void testGetOrder_NotFound() {
        // Arrange
        when(ordersRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ordersService.getOrder(1L);
        });

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testCreateOrder_ItemNotFound() throws Exception {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            ordersService.createOrder(order);
        });

        assertEquals("Item Not Found", exception.getMessage());
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        item.setStock(100L);  // Ensure stock is sufficient
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(ordersRepository.save(order)).thenReturn(order);

        // Act
        Orders result = ordersService.createOrder(order);

        // Assert
        verify(itemRepository, times(1)).save(item);  // Ensure item stock is updated
        verify(ordersRepository, times(1)).save(order);  // Ensure order is saved
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pen", result.getItem().getName());
    }

    @Test
    void testUpdateOrder_Success() {
        // Arrange
        Orders updatedOrder = new Orders();
        updatedOrder.setItem(item);
        updatedOrder.setQty(15L);
        updatedOrder.setPrice(BigDecimal.valueOf(75));

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));
        when(ordersRepository.save(order)).thenReturn(order);

        // Act
        Orders result = ordersService.updateOrder(1L, updatedOrder);

        // Assert
        verify(ordersRepository, times(1)).findById(1L);
        verify(ordersRepository, times(1)).save(order);
        assertEquals(15L, result.getQty());
        assertEquals(BigDecimal.valueOf(75), result.getPrice());
    }

    @Test
    void testUpdateOrder_NotFound() {
        // Arrange
        Orders updatedOrder = new Orders();
        updatedOrder.setItem(item);
        updatedOrder.setQty(15L);
        updatedOrder.setPrice(BigDecimal.valueOf(75));

        when(ordersRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrder(1L, updatedOrder);
        });

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    void testDeleteOrder_Success() {
        // Arrange
        when(ordersRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        ordersService.deleteOrder(1L);

        // Assert
        verify(ordersRepository, times(1)).delete(order);
    }

    @Test
    void testDeleteOrder_NotFound() {
        // Arrange
        when(ordersRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ordersService.deleteOrder(1L);
        });

        assertEquals("Order not found", exception.getMessage());
    }
}
