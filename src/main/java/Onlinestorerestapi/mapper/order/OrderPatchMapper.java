package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.entity.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class OrderPatchMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergeOrderPatchDTOIntoOrder(OrderPatchDTO orderPatchDTO, @MappingTarget Order order);
}
