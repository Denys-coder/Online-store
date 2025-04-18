package Onlinestore.mapper.order;

import Onlinestore.dto.order.GetOrderDTO;
import Onlinestore.entity.Order;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class GetOrderMapper {

    public abstract GetOrderDTO orderToGetOrderDTO(Order order);
}
