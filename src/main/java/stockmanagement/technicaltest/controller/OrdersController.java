package stockmanagement.technicaltest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockmanagement.technicaltest.model.entity.Orders;
import stockmanagement.technicaltest.repository.OrdersRepository;
import stockmanagement.technicaltest.service.OrdersService;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    @Autowired
    private OrdersService orderService;

    @Autowired
    private OrdersRepository ordersRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    public ResponseEntity<?> listOrders(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try{
            if (page != 0) {
                page = -1;
            }
            Pageable pageable = PageRequest.of(page, size);
            List<Orders>orders = orderService.getAllOrders(pageable);
            if (orders.isEmpty()) {
                resp.put("status", false);
                resp.put("message", "Orders Not Found");

                return ResponseEntity.status(404).body(resp);
            } else {
                long totalRecords;
                totalRecords = ordersRepository.count();

                resp.put("status", true);
                resp.put("message", "success");
                resp.put("totalCount", totalRecords);
                resp.put("page", page);
                resp.put("size", size);
                resp.put("data", orders);
                return ResponseEntity.ok().body(resp);
            }
        }catch (Exception ex){
            resp.put("status", false);
            resp.put("message", ex.getMessage());

            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody Orders order) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try {
            validateOrders(order);
            Orders createdOrder = orderService.createOrder(order);
            resp.put("status", true);
            resp.put("message", "Successfully Save Order!");
            resp.put("id", createdOrder.getId());
            return ResponseEntity.status(201).body(resp);
        } catch (Exception ex) {
            resp.put("status", false);
            resp.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Orders> updateOrder(@PathVariable Long id, @RequestBody Orders order) {
        try {
            Orders updatedOrder = orderService.updateOrder(id, order);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public static void validateOrders(Orders orders)throws Exception{
        if(orders.getOrderNo() == null){
            throw new Exception("OrderNo is null");
        }
        if(orders.getItem() == null){
            throw new Exception("Item is null");
        }

        if (orders.getQty() == null) {
            throw new Exception("Quantity is null");
        }

        if (orders.getQty() <= 0) {
            throw new Exception("Quantity must be greater than zero");
        }

        if (orders.getPrice() == null) {
            throw new Exception("Price is null");
        }
    }
}
