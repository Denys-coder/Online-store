package Onlinestore.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;

@Entity
@Table(name = "items")
public class Item
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int id;
    
    @Getter
    @Setter
    @Column(nullable = false)
    @Size(min = 1, max = 64)
    private String name;
    
    @Getter
    @Setter
    @NotNull
    @Min(0)
    private double price;
    
    @Getter
    @Setter
    @NotNull
    @Min(0)
    private int amount;

    @Getter
    @Setter
    private String description;
    
    @Getter
    @Setter
    private String logoName; // only filename without directory
    
    @Getter
    @Setter
    @ElementCollection
    private Set<String> imageNames; // only filenames without directories
    
    @Getter
    @Setter
    @ElementCollection
    private Map<String, String> specs;
    
    public Item()
    {
        imageNames = new HashSet<>();
        specs = new LinkedHashMap<>();
    }
    
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
    
    @Override
    public boolean equals(Object obj)
    {
        return id == ((Item) obj).getId();
    }
}