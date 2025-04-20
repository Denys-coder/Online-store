package Onlinestore.repository;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import Onlinestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    void deleteOrdersByItem(Item item);

    List<Order> findByUser(User user);

    @Transactional
    void deleteOrdersByUser(User user);
}
