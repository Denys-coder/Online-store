package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
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

        itemUtils.saveImagesToFolder(allImages, allImageNames);

        itemRepository.save(item);

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
            throw new ApiException(HttpStatus.BAD_REQUEST, "Item name number should be unique or the same");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No such item");
        }

        Item item = optionalItem.get();

        List<String> allOldImageNames = new ArrayList<>();
        allOldImageNames.add(item.getLogoName());
        allOldImageNames.addAll(item.getImageNames());
        itemUtils.deleteImagesFromFolder(allOldImageNames);

        itemMapper.itemUpdateDTOToItem(itemUpdateDTO, item, images.size());

        List<MultipartFile> allImages = new ArrayList<>();
        allImages.add(logo);
        allImages.addAll(images);

        List<String> allNewImageNames = new ArrayList<>();
        allNewImageNames.add(item.getLogoName());
        allNewImageNames.addAll(item.getImageNames());

        itemUtils.swapItems(allOldImageNames, allImages, allNewImageNames);

        itemRepository.save(item);

    }

}
