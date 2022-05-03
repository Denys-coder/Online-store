package Onlinestore.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Min;

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
    @JoinColumn
    private int userId;
    
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;
    
    @Getter
    @Setter
    @Min(0)
    private int amount;
    
    public Order(Item item, int amount, int userId)
    {
        this.item = item;
        this.amount = amount;
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Order))
        {
            return false;
        }
        
        Order secondOrder = (Order) obj;
        return userId == secondOrder.getUserId() && item.equals(secondOrder.getItem());
    }
}
