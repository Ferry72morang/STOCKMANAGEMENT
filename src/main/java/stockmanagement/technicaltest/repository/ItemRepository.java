package stockmanagement.technicaltest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import stockmanagement.technicaltest.model.entity.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    @Query("SELECT t FROM Item t")
    List<Item>findAllWithPagination(Pageable pageable);
}
