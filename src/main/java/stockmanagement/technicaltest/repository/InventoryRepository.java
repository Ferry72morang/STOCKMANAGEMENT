package stockmanagement.technicaltest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import stockmanagement.technicaltest.model.entity.Inventory;
import stockmanagement.technicaltest.model.entity.Item;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    @Query("SELECT t FROM Inventory t")
    List<Inventory> findAllWithPagination(Pageable pageable);
}
