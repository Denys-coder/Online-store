package Onlinestorerestapi.service.item;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.exception.BadRequestException;
import Onlinestorerestapi.exception.NotFoundException;
import Onlinestorerestapi.mapper.ItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.image.ImageStorageService;
import Onlinestorerestapi.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ItemResponseDTO createItem(ItemCreateDTO itemCreateDTO, MultipartFile logo, List<MultipartFile> pictures) {
        if (itemRepository.existsByName(itemCreateDTO.getName())) {
            throw new BadRequestException("Item name should be unique", Collections.emptyMap());
        }

        Item item = itemMapper.itemCreateDTOToItem(itemCreateDTO, pictures.size());
        List<MultipartFile> logoAndPictures = imageUtils.combineLogoAndPictures(logo, pictures);
        List<String> logoAndPicturesNames = imageUtils.combineLogoAndPictureNames(item.getLogoName(), item.getPictureNames());

        itemRepository.save(item);
        imageStorageService.saveImagesToFolder(logoAndPictures, logoAndPicturesNames);

        return itemMapper.itemToItemResponseDTO(item, false);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ItemResponseDTO updateItem(int itemId, ItemUpdateDTO itemUpdateDTO, MultipartFile logo, List<MultipartFile> pictures) {
        validateItemIdMatch(itemId, itemUpdateDTO.getId());
        validateItemNameUniqueOrSame(itemUpdateDTO.getName(), itemId);

        Item item = getItemByIdOrThrow(itemId);

        List<String> oldLogoAndPictureNames = imageUtils.combineExistingLogoAndPictureNames(item, true, true);

        itemMapper.itemUpdateDTOToItem(itemUpdateDTO, item, pictures.size());
        itemRepository.save(item);

        List<MultipartFile> newLogoAndPictures = imageUtils.combineLogoAndPictures(logo, pictures);
        List<String> newLogoAndPictureNames = imageUtils.combineLogoAndPictureNames(item.getLogoName(), item.getPictureNames());

        imageStorageService.swapImages(oldLogoAndPictureNames, newLogoAndPictures, newLogoAndPictureNames);

        return itemResponseBuilderService.getItemResponseDTOsByItems(List.of(item)).get(0);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Item patchItem(int itemId, ItemPatchDTO itemPatchDTO, MultipartFile logo, List<MultipartFile> pictures) {
        validateItemIdMatch(itemId, itemPatchDTO.getId());

        if (itemPatchDTO.getName() != null) {
            validateItemNameUniqueOrSame(itemPatchDTO.getName(), itemId);
        }

        Item item = getItemByIdOrThrow(itemId);
        List<String> oldLogoAndPicturesNames = imageUtils.combineExistingLogoAndPictureNames(item, logo != null, pictures != null);

        itemMapper.itemPatchDTOToItem(itemPatchDTO, item, logo, pictures);
        itemRepository.save(item);

        List<MultipartFile> newLogoAndPictures = imageUtils.combineLogoAndPictures(logo, pictures);
        List<String> newLogoAndPictureNames = imageUtils.combineLogoAndPictureNames(
                logo != null ? item.getLogoName() : null,
                pictures != null ? item.getPictureNames() : Collections.emptyList()
        );

        imageStorageService.swapImages(oldLogoAndPicturesNames, newLogoAndPictures, newLogoAndPictureNames);

        return item;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteItem(int itemId) {
        Item item = getItemByIdOrThrow(itemId);
        List<String> logoAndPictureNamesToDelete = imageUtils.combineLogoAndPictureNames(item.getLogoName(), item.getPictureNames());

        orderRepository.deleteOrdersByItem(item);
        itemRepository.deleteById(itemId);
        imageStorageService.deleteImagesFromFolder(logoAndPictureNamesToDelete);
    }

    // ======= PRIVATE HELPERS =======

    private Item getItemByIdOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("No such item"));
    }

    private void validateItemIdMatch(int pathId, int bodyId) {
        if (pathId != bodyId) {
            throw new BadRequestException("Item id in the path and in the body should match", Collections.emptyMap());
        }
    }

    private void validateItemNameUniqueOrSame(String name, int itemId) {
        if (itemRepository.existsByName(name)) {
            String existingName = itemRepository.findById(itemId)
                    .map(Item::getName)
                    .orElse("");
            if (!name.equals(existingName)) {
                throw new BadRequestException("Item name should be unique or the same", Collections.emptyMap());
            }
        }
    }
}
