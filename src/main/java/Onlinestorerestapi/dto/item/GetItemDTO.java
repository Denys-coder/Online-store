package Onlinestorerestapi.dto.item;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class GetItemDTO {
    private String name;

    private Double price;

    private Integer amount;

    private String description;

    private String logoName;

    private Set<String> imageNames;

    private Map<String, String> specs;

    private Boolean ordered;
}
