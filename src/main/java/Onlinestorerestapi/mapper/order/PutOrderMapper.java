package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.PutOrderDTO;
import Onlinestorerestapi.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class PutOrderMapper {

    public abstract void mergePutOrderDTOIntoOrder(PutOrderDTO putOrderDTO, @MappingTarget Order order);
}
