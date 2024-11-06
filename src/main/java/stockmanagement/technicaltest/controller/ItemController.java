package stockmanagement.technicaltest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.ItemRepository;
import stockmanagement.technicaltest.service.ItemService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Optional<Item> item = itemService.getItem(id);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> listItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try {
            if (page != 0) {
                page = -1;
            }
            Pageable pageable = PageRequest.of(page, size);
            List<Item> items = itemService.getItems(pageable);
            if (items.isEmpty()) {
                resp.put("status", false);
                resp.put("message", "Items Not Found");

                return ResponseEntity.status(404).body(resp);
            } else {
                long totalRecords;
                totalRecords = itemRepository.count();

                resp.put("status", true);
                resp.put("message", "success");
                resp.put("totalCount", totalRecords);
                resp.put("page", page);
                resp.put("size", size);
                resp.put("data", items);
                return ResponseEntity.ok().body(resp);
            }
        } catch (Exception ex) {
            resp.put("status", false);
            resp.put("message", ex.getMessage());

            return ResponseEntity.badRequest().body(resp);
        }
    }


    @PostMapping
    public ResponseEntity<?> saveItem(@RequestBody Item item) {
        LinkedHashMap<String, Object> resp = new LinkedHashMap<>();
        try{
            validateItems(item);
            Item savedItem = itemService.saveItem(item);
            resp.put("status", true);
            resp.put("message", "Successfully Save Item!");
            resp.put("id", savedItem.getId());
            return ResponseEntity.status(201).body(resp);
        }catch(Exception ex){
            resp.put("status", false);
            resp.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> editItem(@PathVariable Long id, @RequestBody Item item) {
        item.setId(id); // Mengatur ID item dari path
        Item updatedItem = itemService.saveItem(item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    public static void validateItems(Item item)throws Exception{
        if(item.getName() == null){
            throw new Exception("Name is null");
        }
        if(item.getPrice() == null){
            throw new Exception("Price is null");
        }
    }


}
