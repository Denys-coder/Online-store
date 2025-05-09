package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.util.ItemUtils;
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
    private final ItemUtils itemUtils;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public ItemResponseDTO getItemResponseDTO(int itemId) {

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Item not found");
        }
        Item item = itemOptional.get();

        List<Item> items = List.of(item);
        List<ItemResponseDTO> itemResponseDTOs = itemHelperService.getItemResponseDTOsByItems(items);

        return itemResponseDTOs.get(0);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getItemResponseDTOs() {
        return itemHelperService.getItemResponseDTOsByItems(itemRepository.findAll());
    }

    @Transactional
    public Item createItem(ItemCreateDTO itemCreateDTO, MultipartFile logo, List<MultipartFile> images) {

        if (itemRepository.existsByName(itemCreateDTO.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Item name should be unique");
        }

        Item item = itemMapper.itemCreateDTOToItem(itemCreateDTO, images.size());

        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(logo);
        allImages.addAll(images);

        List<String> allImageNames = new ArrayList<>();
        allImageNames.add(item.getLogoName());
        allImageNames.addAll(item.getImageNames());

        itemRepository.save(item);

        // first you need to finish working with item and only then work with pictures because
        // all actions with the database will be rolled back automatically if there is an error when working with pictures,
        // but actions with pictures will not be rolled back automatically if there is a problem with the database
        itemUtils.saveImagesToFolder(allImages, allImageNames);

        return item;
    }

    @Transactional
    public void updateItem(int itemId, ItemUpdateDTO itemUpdateDTO, MultipartFile logo, List<MultipartFile> images) {

        if (itemId != itemUpdateDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Item id in the path and in the body should match");
        }

        String itemName = itemUpdateDTO.getName();
        if (itemRepository.existsByName(itemName) // if it doesn't exist then findById() won't be called
                && !itemName.equals(itemRepository.findById(itemId).get().getName())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Item name should be unique or the same");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No such item");
        }

        Item item = optionalItem.get();

        // save all old file names
        List<String> allOldImageNames = new ArrayList<>();
        allOldImageNames.add(item.getLogoName());
        allOldImageNames.addAll(item.getImageNames());

        itemMapper.itemUpdateDTOToItem(itemUpdateDTO, item, images.size());

        // save all new images
        List<MultipartFile> allNewImages = new ArrayList<>();
        allNewImages.add(logo);
        allNewImages.addAll(images);

        // save all new image names
        List<String> allNewImageNames = new ArrayList<>();
        allNewImageNames.add(item.getLogoName());
        allNewImageNames.addAll(item.getImageNames());

        itemRepository.save(item);

        // first you need to finish working with item and only then work with pictures because
        // all actions with the database will be rolled back automatically if there is an error when working with pictures,
        // but actions with pictures will not be rolled back automatically if there is a problem with the database
        itemUtils.swapImages(allOldImageNames, allNewImages, allNewImageNames);
    }

    @Transactional
    public void patchItem(int itemId, ItemPatchDTO itemPatchDTO, MultipartFile logo, List<MultipartFile> images) {

        if (itemId != itemPatchDTO.getId()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Item id in the path and in the body should match");
        }

        String itemName = itemPatchDTO.getName();
        if (itemName != null
                && itemRepository.existsByName(itemName) // if it doesn't exist then findById() won't be called
                && !itemName.equals(itemRepository.findById(itemId).get().getName())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Item name should be unique or the same");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No such item");
        }

        Item item = optionalItem.get();

        // save all old file names
        List<String> allOldImageNames = new ArrayList<>();
        if (logo != null) {
            allOldImageNames.add(item.getLogoName());
        }
        if (images != null) {
            allOldImageNames.addAll(item.getImageNames());
        }

        itemMapper.itemPatchDTOToItem(itemPatchDTO, item, logo, images);

        itemRepository.save(item);

        // save all new images
        List<MultipartFile> allNewImages = new ArrayList<>();
        if (logo != null) {
            allNewImages.add(logo);
        }
        if (images != null) {
            allNewImages.addAll(images);
        }

        // save all new image names
        List<String> allNewImageNames = new ArrayList<>();
        if (logo != null) {
            allNewImageNames.add(item.getLogoName());
        }
        if (images != null) {
            allNewImageNames.addAll(item.getImageNames());
        }

        // first you need to finish working with item and only then work with pictures because
        // all actions with the database will be rolled back automatically if there is an error when working with pictures,
        // but actions with pictures will not be rolled back automatically if there is a problem with the database
        itemUtils.swapImages(allOldImageNames, allNewImages, allNewImageNames);
    }

    @Transactional
    public void deleteItem(int itemId) {

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No such item");
        }

        Item item = optionalItem.get();

        List<String> itemNamesToDelete = new ArrayList<>();
        itemNamesToDelete.add(item.getLogoName());
        itemNamesToDelete.addAll(item.getImageNames());

        orderRepository.deleteOrdersByItem(item);
        itemRepository.deleteById(itemId);

        itemUtils.deleteImagesFromFolder(itemNamesToDelete);
    }

}
