package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.entity.Item;
import Onlinestorerestapi.service.ItemService;
import Onlinestorerestapi.validation.annotation.item.Image;
import Onlinestorerestapi.validation.annotation.item.ImageArray;
import Onlinestorerestapi.validation.annotation.item.MaxFileCount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping({"/{itemId}"})
    public ResponseEntity<?> getItem(@PathVariable int itemId) {

        ItemResponseDTO itemResponseDTO = itemService.getItemResponseDTO(itemId);
        return ResponseEntity.ok(itemResponseDTO);
    }

    @GetMapping
    public ResponseEntity<?> getItems() {

        List<ItemResponseDTO> itemResponseDTOs = itemService.getItemResponseDTOs();
        return ResponseEntity.ok(itemResponseDTOs);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestPart("item") @Valid ItemCreateDTO itemCreateDTO,
                                      @RequestPart("logo") @Image MultipartFile logo,
                                      @RequestPart("images") @ImageArray @MaxFileCount(maxFileAmount = 10) List<MultipartFile> images) throws URISyntaxException {

        Item item = itemService.createItem(itemCreateDTO, logo, images);

        return ResponseEntity.created(new URI("/items/" + item.getId())).build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable int itemId,
                                        @RequestPart("item") @Valid ItemUpdateDTO itemUpdateDTO,
                                        @RequestPart("logo") @Image MultipartFile logo,
                                        @RequestPart("images") @ImageArray @MaxFileCount(maxFileAmount = 10) List<MultipartFile> images) {

        itemService.updateItem(itemId, itemUpdateDTO, logo, images);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> patchItem(@PathVariable int itemId,
                                       @RequestPart(name = "item", required = false) @Valid ItemPatchDTO itemPatchDTO,
                                       @RequestPart(name = "logo", required = false) @Image MultipartFile logo,
                                       @RequestPart(name = "images", required = false) @ImageArray @MaxFileCount(maxFileAmount = 10) List<MultipartFile> images) {

        itemService.patchItem(itemId, itemPatchDTO, logo, images);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping({"/{itemId}"})
    public ResponseEntity<?> deleteItem(@PathVariable int itemId) {

        itemService.deleteItem(itemId);

        return ResponseEntity.noContent().build();
    }
}
