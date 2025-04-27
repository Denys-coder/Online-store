package Onlinestorerestapi.dto.order;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutOrderDTO {

    @NotNull
    private Integer id;

    @NotNull
    private Integer itemId;

    @DecimalMin(value = "1", message = "amount should be larger than 1")
    @DecimalMax(value = "1000", message = "amount should be smaller than 1000")
    private Integer amount;

}
