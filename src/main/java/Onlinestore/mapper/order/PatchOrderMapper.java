package Onlinestore.mapper.order;

import Onlinestore.dto.order.PatchOrderDTO;
import Onlinestore.entity.Order;
import lombok.NoArgsConstructor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class PatchOrderMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergePatchOrderDTOIntoOrder(PatchOrderDTO patchOrderDTO, @MappingTarget Order order);

}
