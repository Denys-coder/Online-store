package Onlinestorerestapi.mapper;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.dto.order.OrderUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    public abstract OrderResponseDTO orderToOrderResponseDTO(Order order);

    @Mapping(target = "amount", source = "orderCreateDTO.amount")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    public abstract Order orderCreateDTOToOrder(OrderCreateDTO orderCreateDTO, Item item, User user);

    public abstract void mergeOrderUpdateDTOIntoOrder(OrderUpdateDTO orderUpdateDTO, @MappingTarget Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergeOrderPatchDTOIntoOrder(OrderPatchDTO orderPatchDTO, @MappingTarget Order order);

}
