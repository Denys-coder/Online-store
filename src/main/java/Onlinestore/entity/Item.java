package Onlinestore.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity(name = "item")
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
    @NotBlank
    @Size(min = 1, max = 64)
    private String name;
    
    @Getter
    @Setter
    @NotNull
    private double price;
    
    @Getter
    @Setter
    @NotNull
    private int amount;

    @Getter
    @Setter
    // null if empty
    private String description;
    
    @Getter
    @Setter
    // only filename without directory
    // null if empty
    private String logoName;
    
    @Getter
    @Setter
    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    // only filenames without directories
    // null if empty
    private Set<String> imageNames;
    
    @Getter
    @Setter
    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    // null if empty
    private Map<String, String> specs;
    
    public Item()
    {
        description = "";
        imageNames = new HashSet<>();
        specs = new HashMap<>();
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
    
    public void addImageName(String imageName)
    {
        imageNames.add(imageName);
    }
}