package Onlinestore.dto.order;

import Onlinestore.entity.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOrderDTO {

    private Integer id;

    private Item item;

    private Integer amount;
}
