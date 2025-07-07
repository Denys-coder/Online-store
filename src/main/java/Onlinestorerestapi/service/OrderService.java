package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.dto.order.OrderUpdateDTO;
import Onlinestorerestapi.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponseDTO getOrderResponseDTO(int orderId, int userId);

    List<OrderResponseDTO> getOrderResponseDTOs(int userId);

    OrderResponseDTO createOrder(OrderCreateDTO orderCreateDTO, int userId);

    Order updateOrder(int orderId, OrderUpdateDTO orderUpdateDTO, int userId);

    void patchOrder(int orderId, OrderPatchDTO orderPatchDTO, int userId);

    void deleteOrder(int orderId, int userId);

    void deleteOrders(int userId);

    void fulfillOrders(int userId);
}
