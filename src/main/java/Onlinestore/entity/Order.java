package Onlinestore.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int id;
    
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    
    @Getter
    @Setter
    private int amount;
    
    public Order(Item item, int amount)
    {
        this.item = item;
        this.amount = amount;
    }
}
