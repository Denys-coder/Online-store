package Onlinestore.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Setter
@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn
    private int userId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;

    @Min(0)
    private int amount;

    public Order(Item item, int amount, int userId) {
        this.item = item;
        this.amount = amount;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Order)) {
            return false;
        }

        Order secondOrder = (Order) obj;
        return userId == secondOrder.getUserId() && item.equals(secondOrder.getItem());
    }
}
