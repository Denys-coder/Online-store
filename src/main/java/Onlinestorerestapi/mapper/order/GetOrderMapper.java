package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.GetOrderDTO;
import Onlinestorerestapi.entity.Order;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class GetOrderMapper {

    public abstract GetOrderDTO orderToGetOrderDTO(Order order);
}
