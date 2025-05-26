package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.image.ImageStorageService;
import Onlinestorerestapi.util.ImageUtils;
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
    private final ImageStorageService imageStorageService;
    private final OrderRepository orderRepository;
    private final ItemResponseBuilderService itemResponseBuilderService;
    private final ImageUtils imageUtils;

    @Transactional(readOnly = true)
    public ItemResponseDTO getItemResponseDTO(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        return itemResponseBuilderService.getItemResponseDTOsByItems(List.of(item)).get(0);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getItemResponseDTOs() {
        return itemResponseBuilderService.getItemResponseDTOsByItems(itemRepository.findAll());
    }

    @Transactional
    public Item createItem(ItemCreateDTO dto, MultipartFile logo, List<MultipartFile> pictures) {
        if (itemRepository.existsByName(dto.getName())) {
            throw new ApiException(HttpStatus.CONFLICT, "Item name should be unique");
        }

        Item item = itemMapper.itemCreateDTOToItem(dto, pictures.size());
        List<MultipartFile> logoAndPictures = imageUtils.combineLogoAndImages(logo, pictures);
        List<String> logoAndPicturesNames = imageUtils.combineLogoAndImageNames(item.getLogoName(), item.getPictureNames());

        itemRepository.save(item);
        imageStorageService.saveImagesToFolder(logoAndPictures, logoAndPicturesNames);

        return item;
    }

    @Transactional
    public void updateItem(int itemId, ItemUpdateDTO dto, MultipartFile logo, List<MultipartFile> pictures) {
        validateItemIdMatch(itemId, dto.getId());
        validateItemNameUniqueOrSame(dto.getName(), itemId);

        Item item = getItemByIdOrThrow(itemId);

        List<String> oldLogoAndPictureNames = imageUtils.combineExistingLogoAndImageNames(item, true, true);

        itemMapper.itemUpdateDTOToItem(dto, item, pictures.size());
        itemRepository.save(item);

        List<MultipartFile> newLogoAndPictures = imageUtils.combineLogoAndImages(logo, pictures);
        List<String> newLogoAndPictureNames = imageUtils.combineLogoAndImageNames(item.getLogoName(), item.getPictureNames());

        imageStorageService.swapImages(oldLogoAndPictureNames, newLogoAndPictures, newLogoAndPictureNames);
    }

    @Transactional
    public void patchItem(int itemId, ItemPatchDTO dto, MultipartFile logo, List<MultipartFile> pictures) {
        validateItemIdMatch(itemId, dto.getId());

        if (dto.getName() != null) {
            validateItemNameUniqueOrSame(dto.getName(), itemId);
        }

        Item item = getItemByIdOrThrow(itemId);
        List<String> oldLogoAndImageNames = imageUtils.combineExistingLogoAndImageNames(item, logo != null, pictures != null);

        itemMapper.itemPatchDTOToItem(dto, item, logo, pictures);
        itemRepository.save(item);

        List<MultipartFile> newLogoAndImages = imageUtils.combineLogoAndImages(logo, pictures);
        List<String> newLogoAndImageNames = imageUtils.combineLogoAndImageNames(
                logo != null ? item.getLogoName() : null,
                pictures != null ? item.getPictureNames() : Collections.emptyList()
        );

        imageStorageService.swapImages(oldLogoAndImageNames, newLogoAndImages, newLogoAndImageNames);
    }

    @Transactional
    public void deleteItem(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        List<String> logoAndImageNamesToDelete = imageUtils.combineLogoAndImageNames(item.getLogoName(), item.getPictureNames());

        orderRepository.deleteOrdersByItem(item);
        itemRepository.deleteById(itemId);
        imageStorageService.deleteImagesFromFolder(logoAndImageNamesToDelete);
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
}
