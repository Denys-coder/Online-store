package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GetOrderMapper {

    public abstract OrderResponseDTO orderToOrderResponseDTO(Order order);
}
