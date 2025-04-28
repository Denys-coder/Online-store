package Onlinestorerestapi.mapper.order;

import Onlinestorerestapi.dto.order.PatchOrderDTO;
import Onlinestorerestapi.entity.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public abstract class PatchOrderMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergePatchOrderDTOIntoOrder(PatchOrderDTO patchOrderDTO, @MappingTarget Order order);

}
