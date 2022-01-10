package Onlinestore.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity(name = "item")
@Table(name = "items")
@NoArgsConstructor
public class Item
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int id;
    
    @Getter
    @Setter
    private String name;
    
    @Getter
    @Setter
    private double price;
    
    @Getter
    @Setter
    private int amount;

    @Getter
    @Setter
    private String description;
    
    @Getter
    @Setter
    private String logoName;
    
    @Getter
    @Setter
    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<String> imageNames;
    
    @Getter
    @Setter
    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<String, String> specs;
    
    public Item(String name, double price, int amount, String description, String logoName, Set<String> imageNames, Map<String, String> specs)
    {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.logoName = logoName;
        this.imageNames = imageNames;
        this.specs = specs;
    }
}