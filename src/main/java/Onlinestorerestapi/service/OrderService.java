package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.order.OrderCreateDTO;
import Onlinestorerestapi.dto.order.OrderPatchDTO;
import Onlinestorerestapi.dto.order.OrderResponseDTO;
import Onlinestorerestapi.dto.order.OrderUpdateDTO;
import Onlinestorerestapi.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponseDTO getOrderResponseDTO(int orderId);

    List<OrderResponseDTO> getOrderResponseDTOs();

    Order createOrder(OrderCreateDTO orderCreateDTO);

    void updateOrder(int orderId, OrderUpdateDTO orderUpdateDTO);

    void patchOrder(int orderId, OrderPatchDTO orderPatchDTO);

    void deleteOrder(int orderId);

    void deleteOrders();

    void fulfillOrders();
}
