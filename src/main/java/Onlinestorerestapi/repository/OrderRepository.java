package Onlinestorerestapi.repository;

import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    void deleteOrdersByItem(Item item);

    List<Order> findByUser(User user);

    @Transactional
    void deleteOrdersByUser(User user);
}
