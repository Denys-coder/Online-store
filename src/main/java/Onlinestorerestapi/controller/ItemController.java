package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.item.GetItemMapper;
import Onlinestorerestapi.mapper.item.PatchItemMapper;
import Onlinestorerestapi.mapper.item.PostItemMapper;
import Onlinestorerestapi.mapper.item.PutItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.service.ItemService;
import Onlinestorerestapi.service.UserService;
import Onlinestorerestapi.util.ItemUtils;
import Onlinestorerestapi.validation.annotation.item.Image;
import Onlinestorerestapi.validation.annotation.item.ImageArray;
import Onlinestorerestapi.validation.annotation.item.MaxFileCount;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PostItemMapper postItemMapper;
    private final ItemUtils itemUtils;
    private final GetItemMapper getItemMapper;
    private final PutItemMapper putItemMapper;
    private final PatchItemMapper patchItemMapper;
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping({"/{itemId}"})
    public ResponseEntity<?> getItem(@PathVariable int itemId) {

        Item item = itemService.getItem(itemId);

        boolean ordered = itemService.itemOrdered(item);

        ItemResponseDTO itemResponseDTO = getItemMapper.itemToItemResponseDTO(item, ordered);
        return ResponseEntity.ok(itemResponseDTO);
    }

    @GetMapping
    public ResponseEntity<?> getItems() {

        List<Item> items = itemRepository.findAll();
        List<ItemResponseDTO> itemResponseDTOList = new ArrayList<>();

        for (Item item : items) {
            boolean ordered = false;
            if (userService.isAuthenticated()) {
                User user = userService.getCurrentUser();
                List<Order> orders = orderRepository.findByUser(user);

                // check if the user already bought it
                for (Order order : orders) {
                    if (Objects.equals(order.getItem().getId(), item.getId())) {
                        ordered = true;
                        break;
                    }
                }
            }
            itemResponseDTOList.add(getItemMapper.itemToItemResponseDTO(item, ordered));
        }

        return ResponseEntity.ok(itemResponseDTOList);

    }

    @PostMapping
    public ResponseEntity<?> postItem(@RequestPart("logo") @Image MultipartFile logo,
                                      @RequestPart("images") @ImageArray @MaxFileCount(maxFileAmount = 10) MultipartFile[] images,
                                      @RequestPart("item") @Valid ItemCreateDTO itemCreateDTO) throws URISyntaxException {

        if (itemRepository.existsByName(itemCreateDTO.getName())) {
            return ResponseEntity.badRequest().body("Item name should be unique");
        }

        Item item = postItemMapper.itemCreateDTOToItem(itemCreateDTO, images.length);

        itemUtils.saveImageToFolder(logo, item.getLogoName());
        itemUtils.saveImagesToFolder(images, item.getImageNames());

        itemRepository.save(item);

        return ResponseEntity.created(new URI("/items/" + item.getId())).build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> putItem(@PathVariable int itemId,
                                     @RequestPart("logo") @Image MultipartFile logo,
                                     @RequestPart("images") @ImageArray @MaxFileCount(maxFileAmount = 10) MultipartFile[] images,
                                     @RequestPart("item") @Valid ItemUpdateDTO itemUpdateDTO) {

        if (itemId != itemUpdateDTO.getId()) {
            return ResponseEntity.badRequest().body("Item id in the path and in the body should match");
        }

        String itemName = itemUpdateDTO.getName();
        if (itemRepository.existsByName(itemName)
                && !itemName.equals(itemRepository.findById(itemId).get().getName())) {
            return ResponseEntity.badRequest().body("Item name number should be unique or the same");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();

        itemUtils.deleteImageFromFolder(item.getLogoName());
        itemUtils.deleteImagesFromFolder(item.getImageNames());

        putItemMapper.itemUpdateDTOToItem(itemUpdateDTO, item, images.length);

        itemUtils.saveImageToFolder(logo, item.getLogoName());
        itemUtils.saveImagesToFolder(images, item.getImageNames());

        itemRepository.save(item);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> patchItem(@PathVariable int itemId,
                                       @RequestPart(name = "logo", required = false) @Image MultipartFile logo,
                                       @RequestPart(name = "images", required = false) @ImageArray @MaxFileCount(maxFileAmount = 10) MultipartFile[] images,
                                       @RequestPart(name = "item", required = false) @Valid ItemPatchDTO itemPatchDTO) {

        if (itemId != itemPatchDTO.getId()) {
            return ResponseEntity.badRequest().body("Item id in the path and in the body should match");
        }

        String itemName = itemPatchDTO.getName();
        if (itemName != null
                && itemRepository.existsByName(itemName)
                && !itemName.equals(itemRepository.findById(itemId).get().getName())) {
            return ResponseEntity.badRequest().body("Item name number should be unique or the same");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();
        patchItemMapper.itemPatchDTOToItem(itemPatchDTO, item);

        if (logo != null) {
            itemUtils.deleteImageFromFolder(item.getLogoName());
            UUID uuid = UUID.randomUUID();
            item.setLogoName(uuid.toString());
            itemUtils.saveImageToFolder(logo, item.getLogoName());
        }

        if (images != null) {
            itemUtils.deleteImagesFromFolder(item.getImageNames());
            Set<String> imageNames = new HashSet<>();
            while (imageNames.size() < images.length) {
                imageNames.add(UUID.randomUUID().toString());
            }
            item.setImageNames(imageNames);
            itemUtils.saveImagesToFolder(images, imageNames);

        }

        itemRepository.save(item);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping({"/{itemId}"})
    public ResponseEntity<?> deleteItem(@PathVariable int itemId) {

        Item itemToDelete = itemRepository.getReferenceById(itemId);
        itemUtils.deleteImageFromFolder(itemToDelete.getLogoName());
        itemUtils.deleteImagesFromFolder(itemToDelete.getImageNames());

        orderRepository.deleteOrdersByItem(itemToDelete);
        itemRepository.deleteById(itemId);

        return ResponseEntity.noContent().build();
    }
}
