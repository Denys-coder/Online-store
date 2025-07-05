package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemResponseBuilderServiceImpl implements ItemResponseBuilderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final ItemMapper itemMapper;

    public List<ItemResponseDTO> getItemResponseDTOsByItems(List<Item> items) {

        List<ItemResponseDTO> getItemDTOs = new ArrayList<>();
        List<Boolean> itemsOrderedFlags = new ArrayList<>(Collections.nCopies(items.size(), false));

        if (authService.isAuthenticated()) {

            User user = authService.getCurrentUser();
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

    // ======= PRIVATE HELPERS =======

    private boolean isOrdered(Item item, List<Order> orders) {
        for (Order order : orders) {
            if (Objects.equals(order.getItem().getId(), item.getId())) {
                return true;
            }
        }
        return false;
    }
}
