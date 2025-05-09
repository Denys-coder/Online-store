package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.util.ImageUtils;
import Onlinestorerestapi.validation.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemHelperService itemHelperService;
    private final ItemMapper itemMapper;
    private final ImageUtils imageUtils;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public ItemResponseDTO getItemResponseDTO(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        return itemHelperService.getItemResponseDTOsByItems(List.of(item)).get(0);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getItemResponseDTOs() {
        return itemHelperService.getItemResponseDTOsByItems(itemRepository.findAll());
    }

    @Transactional
    public Item createItem(ItemCreateDTO dto, MultipartFile logo, List<MultipartFile> images) {
        validateItemNameUniqueness(dto.getName());

        Item item = itemMapper.itemCreateDTOToItem(dto, images.size());
        List<MultipartFile> allImages = combineFiles(logo, images);
        List<String> allImageNames = combineNames(item.getLogoName(), item.getImageNames());

        itemRepository.save(item);
        imageUtils.saveImagesToFolder(allImages, allImageNames);

        return item;
    }

    @Transactional
    public void updateItem(int itemId, ItemUpdateDTO dto, MultipartFile logo, List<MultipartFile> images) {
        validateItemIdMatch(itemId, dto.getId());
        validateItemNameUniqueOrSame(dto.getName(), itemId);

        Item item = getItemByIdOrThrow(itemId);

        List<String> oldNames = getExistingImageNames(item, true, true);

        itemMapper.itemUpdateDTOToItem(dto, item, images.size());
        itemRepository.save(item);

        List<MultipartFile> newFiles = combineFiles(logo, images);
        List<String> newNames = combineNames(item.getLogoName(), item.getImageNames());

        imageUtils.swapImages(oldNames, newFiles, newNames);
    }

    @Transactional
    public void patchItem(int itemId, ItemPatchDTO dto, MultipartFile logo, List<MultipartFile> images) {
        validateItemIdMatch(itemId, dto.getId());

        if (dto.getName() != null) {
            validateItemNameUniqueOrSame(dto.getName(), itemId);
        }

        Item item = getItemByIdOrThrow(itemId);
        List<String> oldNames = getExistingImageNames(item, logo != null, images != null);

        itemMapper.itemPatchDTOToItem(dto, item, logo, images);
        itemRepository.save(item);

        List<MultipartFile> newFiles = combineFiles(logo, images);
        List<String> newNames = combineNames(
                logo != null ? item.getLogoName() : null,
                images != null ? item.getImageNames() : Collections.emptyList()
        );

        imageUtils.swapImages(oldNames, newFiles, newNames);
    }

    @Transactional
    public void deleteItem(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        List<String> namesToDelete = combineNames(item.getLogoName(), item.getImageNames());

        orderRepository.deleteOrdersByItem(item);
        itemRepository.deleteById(itemId);
        imageUtils.deleteImagesFromFolder(namesToDelete);
    }

    // ======= PRIVATE HELPERS =======

    private Item getItemByIdOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No such item"));
    }

    private void validateItemNameUniqueness(String name) {
        if (itemRepository.existsByName(name)) {
            throw new ApiException(HttpStatus.CONFLICT, "Item name should be unique");
        }
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

    private List<String> getExistingImageNames(Item item, boolean includeLogo, boolean includeImages) {
        List<String> names = new ArrayList<>();
        if (includeLogo) {
            names.add(item.getLogoName());
        }
        if (includeImages) {
            names.addAll(item.getImageNames());
        }
        return names;
    }

    private List<MultipartFile> combineFiles(MultipartFile logo, List<MultipartFile> images) {
        List<MultipartFile> all = new ArrayList<>();
        if (logo != null) all.add(logo);
        if (images != null) all.addAll(images);
        return all;
    }

    private List<String> combineNames(String logoName, List<String> imageNames) {
        List<String> all = new ArrayList<>();
        if (logoName != null) all.add(logoName);
        if (imageNames != null) all.addAll(imageNames);
        return all;
    }
}