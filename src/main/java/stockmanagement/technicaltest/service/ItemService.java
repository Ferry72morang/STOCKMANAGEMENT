package stockmanagement.technicaltest.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stockmanagement.technicaltest.model.entity.Item;
import stockmanagement.technicaltest.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getItems(Pageable pageable) {
        return itemRepository.findAllWithPagination(pageable);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Optional<Item> getItem(Long id) {
        return itemRepository.findById(id);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
