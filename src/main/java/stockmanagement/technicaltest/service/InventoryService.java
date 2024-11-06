package stockmanagement.technicaltest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stockmanagement.technicaltest.model.entity.Inventory;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.InventoryRepository;
import stockmanagement.technicaltest.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<Inventory> getInventories(Pageable pageable) {
        return inventoryRepository.findAllWithPagination(pageable);
    }

    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getInventory(Long id) {
        return inventoryRepository.findById(id);
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    // Logic untuk memperbarui stok item berdasarkan type
    public void updateStock(Inventory inventory) {
        Item item = itemRepository.findById(inventory.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item with name: " + inventory.getItem().getName() + "not found!"));
         if (inventory.getType().equals("T")) {
             item.setStock(item.getStock() + inventory.getQty());
         } else if (inventory.getType().equals("W")) {
             item.setStock(item.getStock() - inventory.getQty());
         }
         itemRepository.save(item);
    }
}
