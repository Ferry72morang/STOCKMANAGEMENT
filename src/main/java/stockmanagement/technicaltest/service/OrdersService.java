package stockmanagement.technicaltest.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.model.entity.Orders;
import stockmanagement.technicaltest.repository.ItemRepository;
import stockmanagement.technicaltest.repository.OrdersRepository;

import java.util.List;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Orders getOrder(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // Get list all with pagination
    public List<Orders> getAllOrders(Pageable pageable) {
        return ordersRepository.findAllWithPagination(pageable);
    }

    // Create Order
    @Transactional
    public Orders createOrder(Orders order) {
        Item item = itemRepository.findById(order.getItem().getId())
                .orElseThrow(() -> new Exception("Item not found"));
        if (item.getStock() < order.getQty()) {
            throw new Exception("Insufficient stock for item: " + item.getName());
        }

        // Decreased stock item
        item.setStock(item.getStock() - order.getQty());
        itemRepository.save(item);
        return ordersRepository.save(order);
    }

    // Edit Order
    @Transactional
    public Orders updateOrder(Long id, Orders updatedOrder) {
        Orders existingOrder = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setItem(updatedOrder.getItem());
        existingOrder.setQty(updatedOrder.getQty());
        existingOrder.setPrice(updatedOrder.getPrice());

        return ordersRepository.save(existingOrder);
    }

    // Menghapus Order
    public void deleteOrder(Long id) {
        Orders existingOrder = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ordersRepository.delete(existingOrder);
    }
}
