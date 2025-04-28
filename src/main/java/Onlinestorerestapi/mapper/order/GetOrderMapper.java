package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.GetOrderDTO;
import Onlinestorerestapi.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GetOrderMapper {

    public abstract GetOrderDTO orderToGetOrderDTO(Order order);
}
