package stockmanagement.technicaltest.ControllerLayerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stockmanagement.technicaltest.controller.OrdersController;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.model.entity.Orders;
import stockmanagement.technicaltest.repository.OrdersRepository;
import stockmanagement.technicaltest.service.OrdersService;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

    @MockBean
    private OrdersService orderService;

    @MockBean
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrdersController ordersController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ordersController).build();
    }

    @Test
    public void testGetOrderById_Found() throws Exception {
        Orders order = new Orders();
        order.setId(1L);
        when(orderService.getOrder(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(orderService, times(1)).getOrder(1L);
    }


    @Test
    public void testListOrders_Found() throws Exception {
        Orders order = new Orders();
        order.setId(1L);
        when(orderService.getAllOrders(any(Pageable.class)))
                .thenReturn(Collections.singletonList(order));
        when(ordersRepository.count()).thenReturn(1L);

        mockMvc.perform(get("/api/orders?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.totalCount", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(1)));

        verify(orderService, times(1)).getAllOrders(any(Pageable.class));
        verify(ordersRepository, times(1)).count();
    }

    @Test
    public void testListOrders_NotFound() throws Exception {
        when(orderService.getAllOrders(any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders?page=0&size=10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message", is("Orders Not Found")));

        verify(orderService, times(1)).getAllOrders(any(Pageable.class));
    }

    @Test
    public void testUpdateOrder_Success() throws Exception {
        // Siapkan objek Item
        Item item = new Item();
        item.setId(1L);
        item.setStock(1L);
        item.setName("Pen");
        item.setPrice(BigDecimal.valueOf(5));

        // Siapkan objek Orders dengan Item yang sudah dibuat
        Orders updatedOrder = new Orders();
        updatedOrder.setId(1L);
        updatedOrder.setOrderNo("123");
        updatedOrder.setItem(item);
        updatedOrder.setQty(2L);
        updatedOrder.setPrice(BigDecimal.valueOf(50));

        // Simulasikan service yang mengembalikan updatedOrder
        when(orderService.updateOrder(eq(1L), any(Orders.class))).thenReturn(updatedOrder);

        // Konversi objek Orders ke JSON menggunakan ObjectMapper
        String orderJson = new ObjectMapper().writeValueAsString(updatedOrder);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Pen"))
                .andExpect(jsonPath("$.item.price").value(5))
                .andExpect(jsonPath("$.qty").value(2))
                .andExpect(jsonPath("$.price").value(50.0));

        verify(orderService, times(1)).updateOrder(eq(1L), any(Orders.class));
    }

    @Test
    public void testSaveOrder_ValidationError() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderNo\": null, \"item\": {\"id\": 1, \"name\": \"item1\", \"stock\": 1, \"price\": 5}, \"qty\": 2, \"price\": 50.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("OrderNo is null"));
    }

    @Test
    public void testDeleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    public void testDeleteOrder_NotFound() throws Exception {
        doThrow(new RuntimeException("Order not found")).when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).deleteOrder(1L);
    }
}

