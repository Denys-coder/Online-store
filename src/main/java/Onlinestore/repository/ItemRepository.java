package Onlinestore.repository;

import Onlinestore.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>
{
    Item findItemById(int id);
}