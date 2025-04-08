package Onlinestore.dto.item;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class GetItemDTO {
    private String name;

    private double price;

    private int amount;

    private String description;

    private String logoName;

    private Set<String> imageNames;

    private Map<String, String> specs;

    private boolean ordered;
}
