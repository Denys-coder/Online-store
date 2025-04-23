package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.item.GetItemDTO;
import Onlinestorerestapi.dto.item.PatchItemDTO;
import Onlinestorerestapi.dto.item.PostItemDTO;
import Onlinestorerestapi.dto.item.PutItemDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.entity.Order;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.item.GetItemMapper;
import Onlinestorerestapi.mapper.item.PatchItemMapper;
import Onlinestorerestapi.mapper.item.PostItemMapper;
import Onlinestorerestapi.mapper.item.PutItemMapper;
import Onlinestorerestapi.repository.ItemRepository;
import Onlinestorerestapi.repository.OrderRepository;
import Onlinestorerestapi.security.UserPrincipal;
import Onlinestorerestapi.util.ItemUtil;
import Onlinestorerestapi.validation.annotation.item.Image;
import Onlinestorerestapi.validation.annotation.item.ImageArray;
import Onlinestorerestapi.validation.annotation.item.MaxFileCount;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    private final ItemUtil itemUtil;
    private final GetItemMapper getItemMapper;
    private final PutItemMapper putItemMapper;
    private final PatchItemMapper patchItemMapper;

    @GetMapping({"/{itemId}"})
    public ResponseEntity<?> getItem(@PathVariable int itemId) {

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        Item item = itemOptional.get();

        boolean ordered = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
            List<Order> orders = orderRepository.findByUser(user);

            // check if user already bought it
            for (Order order : orders) {
                if (Objects.equals(order.getItem().getId(), item.getId())) {
                    ordered = true;
                    break;
                }
            }
        }

        GetItemDTO getItemDTO = getItemMapper.itemToGetItemDTO(item, ordered);
        return ResponseEntity.ok(getItemDTO);
    }


    @GetMapping
    public ResponseEntity<?> getItems() {

        List<Item> items = itemRepository.findAll();
        List<GetItemDTO> getItemDTOList = new ArrayList<>();

        for (Item item : items) {
            boolean ordered = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null
                    && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                User user = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
                List<Order> orders = orderRepository.findByUser(user);

                // check if user already bought it
                for (Order order : orders) {
                    if (Objects.equals(order.getItem().getId(), item.getId())) {
                        ordered = true;
                        break;
                    }
                }
            }
            getItemDTOList.add(getItemMapper.itemToGetItemDTO(item, ordered));
        }

        return ResponseEntity.ok(getItemDTOList);

    }

    @PostMapping
    public ResponseEntity<?> postItem(@RequestPart("logo") @Image MultipartFile logo,
                                      @RequestPart("images") @ImageArray @MaxFileCount(max = 10) MultipartFile[] images,
                                      @RequestPart("item") @Valid PostItemDTO postItemDTO) throws URISyntaxException {

        Item item = postItemMapper.postItemDTOToItem(postItemDTO, images.length);

        itemUtil.saveImageToFolder(logo, item.getLogoName());
        itemUtil.saveImagesToFolder(images, item.getImageNames());

        itemRepository.save(item);

        return ResponseEntity.created(new URI("/items/" + item.getId())).build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> putItem(@PathVariable int itemId,
                                     @RequestPart("logo") @Image MultipartFile logo,
                                     @RequestPart("images") @ImageArray @MaxFileCount(max = 10) MultipartFile[] images,
                                     @RequestPart("item") @Valid PutItemDTO putItemDTO) {

        if (itemId != putItemDTO.getId()) {
            return ResponseEntity.badRequest().body("Item id in the path and in the body should match");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();

        itemUtil.deleteImageFromFolder(item.getLogoName());
        itemUtil.deleteImagesFromFolder(item.getImageNames());

        putItemMapper.putItemDTOToItem(putItemDTO, item, images.length);

        itemUtil.saveImageToFolder(logo, item.getLogoName());
        itemUtil.saveImagesToFolder(images, item.getImageNames());

        itemRepository.save(item);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> patchMapping(@PathVariable int itemId,
                                          @RequestPart(name = "logo", required = false) @Image MultipartFile logo,
                                          @RequestPart(name = "images", required = false) @ImageArray @MaxFileCount(max = 10) MultipartFile[] images,
                                          @RequestPart(name = "item", required = false) @Valid PatchItemDTO patchItemDTO) {

        if (itemId != patchItemDTO.getId()) {
            return ResponseEntity.badRequest().body("Item id in the path and in the body should match");
        }

        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();
        patchItemMapper.patchItemDTOToItem(patchItemDTO, item);

        if (logo != null) {
            itemUtil.deleteImageFromFolder(item.getLogoName());
            UUID uuid = UUID.randomUUID();
            item.setLogoName(uuid.toString());
            itemUtil.saveImageToFolder(logo, item.getLogoName());
        }

        if (images != null) {
            itemUtil.deleteImagesFromFolder(item.getImageNames());
            Set<String> imageNames = new HashSet<>();
            while (imageNames.size() < images.length) {
                imageNames.add(UUID.randomUUID().toString());
            }
            item.setImageNames(imageNames);
            itemUtil.saveImagesToFolder(images, imageNames);

        }

        itemRepository.save(item);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping({"/{itemId}"})
    public ResponseEntity<?> deleteItem(@PathVariable int itemId) {

        Item itemToDelete = itemRepository.getReferenceById(itemId);
        itemUtil.deleteImageFromFolder(itemToDelete.getLogoName());
        itemUtil.deleteImagesFromFolder(itemToDelete.getImageNames());

        orderRepository.deleteOrdersByItem(itemToDelete);
        itemRepository.deleteById(itemId);

        return ResponseEntity.noContent().build();
    }
}
