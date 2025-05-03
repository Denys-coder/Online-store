package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.OrderUpdateDTO;
import Onlinestorerestapi.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class OrderUpdateMapper {

    public abstract void mergeOrderUpdateDTOIntoOrder(OrderUpdateDTO orderUpdateDTO, @MappingTarget Order order);
}
