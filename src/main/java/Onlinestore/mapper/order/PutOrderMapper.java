package Onlinestore.mapper.order;

import Onlinestore.dto.order.PutOrderDTO;
import Onlinestore.entity.Order;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@NoArgsConstructor(force = true)
@Mapper(componentModel = "spring")
public abstract class PutOrderMapper {

    public abstract void mergePutOrderDTOIntoOrder(PutOrderDTO putOrderDTO, @MappingTarget Order order);
}
