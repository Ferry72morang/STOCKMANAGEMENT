package stockmanagement.technicaltest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import stockmanagement.technicaltest.model.entity.Orders;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT t FROM Orders t")
    List<Orders> findAllWithPagination(Pageable pageable);
}
