package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.util.PictureUtils;
import Onlinestorerestapi.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final PictureUtils pictureUtils;
    private final OrderRepository orderRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public ItemResponseDTO getItemResponseDTO(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        return getItemResponseDTOsByItems(List.of(item)).get(0);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getItemResponseDTOs() {
        return getItemResponseDTOsByItems(itemRepository.findAll());
    }

    @Transactional
    public Item createItem(ItemCreateDTO dto, MultipartFile logo, List<MultipartFile> images) {
        if (itemRepository.existsByName(dto.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Item name should be unique");
        }

        Item item = itemMapper.itemCreateDTOToItem(dto, images.size());
        List<MultipartFile> allPictures = combineFiles(logo, images);
        List<String> allPictureNames = combineNames(item.getLogoName(), item.getImageNames());

        itemRepository.save(item);
        pictureUtils.savePicturesToFolder(allPictures, allPictureNames);

        return item;
    }

    @Transactional
    public void updateItem(int itemId, ItemUpdateDTO dto, MultipartFile logo, List<MultipartFile> images) {
        validateItemIdMatch(itemId, dto.getId());
        validateItemNameUniqueOrSame(dto.getName(), itemId);

        Item item = getItemByIdOrThrow(itemId);

        List<String> oldPictureNames = getExistingPictureNames(item, true, true);

        itemMapper.itemUpdateDTOToItem(dto, item, images.size());
        itemRepository.save(item);

        List<MultipartFile> newPictures = combineFiles(logo, images);
        List<String> newPictureNames = combineNames(item.getLogoName(), item.getImageNames());

        pictureUtils.swapPictures(oldPictureNames, newPictures, newPictureNames);
    }

    @Transactional
    public void patchItem(int itemId, ItemPatchDTO dto, MultipartFile logo, List<MultipartFile> images) {
        validateItemIdMatch(itemId, dto.getId());

        if (dto.getName() != null) {
            validateItemNameUniqueOrSame(dto.getName(), itemId);
        }

        Item item = getItemByIdOrThrow(itemId);
        List<String> oldPictureNames = getExistingPictureNames(item, logo != null, images != null);

        itemMapper.itemPatchDTOToItem(dto, item, logo, images);
        itemRepository.save(item);

        List<MultipartFile> newPictures = combineFiles(logo, images);
        List<String> newPictureNames = combineNames(
                logo != null ? item.getLogoName() : null,
                images != null ? item.getImageNames() : Collections.emptyList()
        );

        pictureUtils.swapPictures(oldPictureNames, newPictures, newPictureNames);
    }

    @Transactional
    public void deleteItem(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        List<String> pictureNamesToDelete = combineNames(item.getLogoName(), item.getImageNames());

        orderRepository.deleteOrdersByItem(item);
        itemRepository.deleteById(itemId);
        pictureUtils.deletePicturesFromFolder(pictureNamesToDelete);
    }

    // ======= PRIVATE HELPERS =======

    private Item getItemByIdOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No such item"));
    }

    private void validateItemIdMatch(int pathId, int bodyId) {
        if (pathId != bodyId) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Item id in the path and in the body should match");
        }
    }

    private void validateItemNameUniqueOrSame(String name, int itemId) {
        if (itemRepository.existsByName(name)) {
            String existingName = itemRepository.findById(itemId)
                    .map(Item::getName)
                    .orElse("");
            if (!name.equals(existingName)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Item name should be unique or the same");
            }
        }
    }

    private List<String> getExistingPictureNames(Item item, boolean includeLogo, boolean includeImages) {
        List<String> pictureNames = new ArrayList<>();
        if (includeLogo) {
            pictureNames.add(item.getLogoName());
        }
        if (includeImages) {
            pictureNames.addAll(item.getImageNames());
        }
        return pictureNames;
    }

    private List<MultipartFile> combineFiles(MultipartFile logo, List<MultipartFile> images) {
        List<MultipartFile> pictures = new ArrayList<>();
        if (logo != null) pictures.add(logo);
        if (images != null) pictures.addAll(images);
        return pictures;
    }

    private List<String> combineNames(String logoName, List<String> imageNames) {
        List<String> pictureNames = new ArrayList<>();
        if (logoName != null) pictureNames.add(logoName);
        if (imageNames != null) pictureNames.addAll(imageNames);
        return pictureNames;
    }

    private List<ItemResponseDTO> getItemResponseDTOsByItems(List<Item> items) {

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

    private boolean isOrdered(Item item, List<Order> orders) {
        for (Order order : orders) {
            if (Objects.equals(order.getItem().getId(), item.getId())) {
                return true;
            }
        }
        return false;
    }
}
