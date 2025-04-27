package Onlinestorerestapi.dto.order;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchOrderDTO {

    @NotNull
    private Integer id;

    @Nullable
    private Integer itemId;

    @Nullable
    @DecimalMin(value = "1", message = "amount should be larger than 1")
    @DecimalMax(value = "1000", message = "amount should be smaller than 1000")
    private Integer amount;

}
