package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.PostOrderDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostOrderMapper {

    @Mapping(target = "amount", source = "postOrderDTO.amount")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    public abstract Order postOrderDTOToOrder(PostOrderDTO postOrderDTO, Item item, User user);
}
