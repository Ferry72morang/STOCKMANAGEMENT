package stockmanagement.technicaltest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockmanagement.technicaltest.model.entity.Inventory;
import stockmanagement.technicaltest.model.entity.Orders;
import stockmanagement.technicaltest.repository.InventoryRepository;
import stockmanagement.technicaltest.service.InventoryService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryService.getInventory(id);
        return inventory.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> listInventories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try{
            if (page != 0) {
                page = -1;
            }
            Pageable pageable = PageRequest.of(page, size);
            List<Inventory> inventories = inventoryService.getInventories(pageable);
            if (inventories.isEmpty()) {
                resp.put("status", false);
                resp.put("message", "Inventories Not Found");

                return ResponseEntity.status(404).body(resp);
            } else {
                long totalRecords;
                totalRecords = inventoryRepository.count();

                resp.put("status", true);
                resp.put("message", "success");
                resp.put("totalCount", totalRecords);
                resp.put("page", page);
                resp.put("size", size);
                resp.put("data", inventories);
                return ResponseEntity.ok().body(resp);
            }
        }catch (Exception ex){
            resp.put("status", false);
            resp.put("message", ex.getMessage());

            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveInventory(@RequestBody Inventory inventory) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try{
            validateInventory(inventory);
            Inventory savedInventory = inventoryService.saveInventory(inventory);
            inventoryService.updateStock(inventory);
            resp.put("status", true);
            resp.put("message", "Successfully Save Inventories!");
            resp.put("id", savedInventory.getId());
            return ResponseEntity.status(201).body(resp);
        }catch (Exception ex){
            resp.put("status", false);
            resp.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<Inventory> editInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        inventory.setId(id);
        Inventory updatedInventory = inventoryService.saveInventory(inventory);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }


    public static void validateInventory(Inventory inventory) throws Exception {
        if (inventory.getItem() == null) {
            throw new Exception("Item is null");
        }

        if (inventory.getQty() == null) {
            throw new Exception("Quantity is null");
        }

        if (inventory.getQty() <= 0) {
            throw new Exception("Quantity must be greater than zero");
        }

        if (inventory.getType() == null) {
            throw new Exception("Type is null");
        }

        if (!"T".equalsIgnoreCase(inventory.getType()) && !"W".equalsIgnoreCase(inventory.getType())) {
            throw new Exception("Type must be either 'T' (Top Up) or 'W' (Withdrawal)");
        }
    }
}
