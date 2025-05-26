package Onlinestorerestapi.dto.item;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ItemResponseDTO {
    private Integer id;

    private String name;

    private Double price;

    private Integer amount;

    private String description;

    private String logoName;

    private Set<String> pictureNames;

    private Map<String, String> specs;

    private Boolean ordered;
}
