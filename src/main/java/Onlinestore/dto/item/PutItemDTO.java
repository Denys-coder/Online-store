package Onlinestore.dto.item;

import Onlinestore.validation.annotation.item.UniqueOrSameItemName;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@UniqueOrSameItemName
public class PutItemDTO {

    @NotNull
    private Integer id;

    @NotBlank(message = "should not be blank or null")
    @Size(min = 2, max = 30, message = "name should be from 2 to 30 symbols")
    private String name;

    @DecimalMin(value = "0.1", message = "price should be larger than 0.1")
    @DecimalMax(value = "1000000.0", message = "price should be smaller than 1000000.0")
    private Double price;

    @DecimalMin(value = "1", message = "amount should be larger than 1")
    @DecimalMax(value = "10000", message = "amount should be smaller than 10000")
    private Integer amount;

    @NotBlank(message = "description should not be blank or null")
    @Size(min = 2, max = 500, message = "description should be from 2 to 500 symbols")
    private String description;

    @NotNull(message = "specs should not be null")
    private Map<String, String> specs;

}
