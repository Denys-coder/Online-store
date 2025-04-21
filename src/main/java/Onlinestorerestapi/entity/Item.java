package Onlinestorerestapi.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, unique = true, length = 10)
    private String logoName; // only filename without directory

    @ElementCollection
    @CollectionTable(name = "item_image_names", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_name",  nullable = false, unique = true, length = 11)
    private Set<String> imageNames; // only filenames without directories

    @ElementCollection
    @CollectionTable(name = "item_specs", joinColumns = @JoinColumn(name = "item_id"))
    @MapKeyColumn(name = "spec_name", nullable = false, length = 30) // Column for the map key
    @Column(name = "spec_value", nullable = false, length = 30) // Column for the map value
    private Map<String, String> specs;

    public Item() {
        imageNames = new HashSet<>();
        specs = new LinkedHashMap<>();
    }

    public Item(String name, double price, int amount, String description, String logoName, Set<String> imageNames, Map<String, String> specs) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.description = description;
        this.logoName = logoName;
        this.imageNames = imageNames;
        this.specs = specs;
    }
}
