package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public ItemResponseDTO getItemResponseDTO(int itemId) {

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item not found");
        }
        Item item = itemOptional.get();

        List<Item> items = List.of(item);
        List<ItemResponseDTO> itemResponseDTOs = getItemResponseDTOsByItems(items);

        return itemResponseDTOs.get(0);
    }

    public List<ItemResponseDTO> getItemResponseDTOs() {
        return getItemResponseDTOsByItems(itemRepository.findAll());
    }

    private List<ItemResponseDTO> getItemResponseDTOsByItems(List<Item> items) {

        List<ItemResponseDTO> getItemDTOs = new ArrayList<>();
        List<Boolean> itemsOrderedFlags = new ArrayList<>(Collections.nCopies(items.size(), false));

        if (userService.isAuthenticated()) {

            User user = userService.getCurrentUser();
            List<Order> orders = orderRepository.findByUser(user);

            // get boolean ordered for each item
            for (int i = 0; i < items.size(); i++) {
                boolean ordered = isOrdered(items.get(i), orders);
                itemsOrderedFlags.add(i, ordered);
            }

        }

        // fill getItemDTOs using item and respective boolean ordered
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            boolean ordered = itemsOrderedFlags.get(i);
            getItemDTOs.add(itemMapper.itemToItemResponseDTO(item, ordered));
        }

        return getItemDTOs;
    }

    private boolean isOrdered(Item item, List<Order> orders) {
        for (Order order : orders) {
            if (Objects.equals(order.getItem().getId(), item.getId())) {
                return true;
            }
        }
        return false;
    }

}
