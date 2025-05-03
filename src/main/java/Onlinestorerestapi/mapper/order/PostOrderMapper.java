package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostOrderMapper {

    @Mapping(target = "amount", source = "orderCreateDTO.amount")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    public abstract Order orderCreateDTOToOrder(OrderCreateDTO orderCreateDTO, Item item, User user);
}
