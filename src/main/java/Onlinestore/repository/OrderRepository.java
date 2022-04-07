package Onlinestore.repository;

import Onlinestore.entity.Item;
import Onlinestore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>
{
    @Transactional
    void deleteOrdersByItem(Item item);
    
    List<Order> getAllByUserId(int userId);
}
