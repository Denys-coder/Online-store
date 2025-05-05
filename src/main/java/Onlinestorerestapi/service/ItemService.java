package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
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
    public Item createItem(ItemCreateDTO itemCreateDTO,
                           MultipartFile logo,
                           List<MultipartFile> images) {

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

}
