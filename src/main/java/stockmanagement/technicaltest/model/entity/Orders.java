package stockmanagement.technicaltest.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "ORDERS")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_NO", nullable = false)
    private String orderNo;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    @Column(name = "QTY", nullable = false)
    private Long qty;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;
}
