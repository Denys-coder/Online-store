package Onlinestorerestapi.dto.order;

import Onlinestorerestapi.entity.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {

    private Integer id;

    private Item item;

    private Integer amount;
}
