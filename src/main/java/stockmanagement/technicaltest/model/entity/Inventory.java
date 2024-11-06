package stockmanagement.technicaltest.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "INVENTORY")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    @Column(name = "QTY", nullable = false)
    private Long qty;

    @Column(name = "TYPE", nullable = false, length = 1)
    private String type;
}
