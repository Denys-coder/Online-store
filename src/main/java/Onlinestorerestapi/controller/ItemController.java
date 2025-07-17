package Onlinestorerestapi.controller;

import Onlinestorerestapi.dto.item.ItemCreateDTO;
import Onlinestorerestapi.dto.item.ItemResponseDTO;
import Onlinestorerestapi.dto.item.ItemPatchDTO;
import Onlinestorerestapi.dto.item.ItemUpdateDTO;
import Onlinestorerestapi.service.item.ItemService;
import Onlinestorerestapi.validation.annotation.item.Image;
import Onlinestorerestapi.validation.annotation.item.ImageArray;
import Onlinestorerestapi.validation.annotation.item.MaxFileCount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Tag(name = "item", description = "Operations related to items")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Get item by id")
    @GetMapping({"/{itemId}"})
    public ResponseEntity<ItemResponseDTO> getItem(@PathVariable int itemId) {

        ItemResponseDTO itemResponseDTO = itemService.getItemResponseDTO(itemId);
        return ResponseEntity.ok(itemResponseDTO);
    }

    @Operation(summary = "Get all items")
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getItems() {

        List<ItemResponseDTO> itemResponseDTOs = itemService.getItemResponseDTOs();
        return ResponseEntity.ok(itemResponseDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create new item",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@RequestPart("item") @Valid ItemCreateDTO itemCreateDTO,
                                        @RequestPart("logo") @Image MultipartFile logo,
                                        @RequestPart("images") @ImageArray @MaxFileCount(maxFileAmount = 10) List<MultipartFile> pictures) throws URISyntaxException {

        ItemResponseDTO itemResponseDTO = itemService.createItem(itemCreateDTO, logo, pictures);

        URI location = new URI("/api/v1/items/" + itemResponseDTO.getId());
        return ResponseEntity
                .created(location)
                .body(itemResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update item (need to specify all fields)",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PutMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> updateItem(@PathVariable int itemId,
                                        @RequestPart("item") @Valid ItemUpdateDTO itemUpdateDTO,
                                        @RequestPart("logo") @Image MultipartFile logo,
                                        @RequestPart("pictures") @ImageArray @MaxFileCount(maxFileAmount = 10) List<MultipartFile> pictures) {

        ItemResponseDTO itemResponseDTO = itemService.updateItem(itemId, itemUpdateDTO, logo, pictures);

        return ResponseEntity.ok().body(itemResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update item (need to specify only fields being updated",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> patchItem(@PathVariable int itemId,
                                       @RequestPart(name = "item", required = false) @Valid ItemPatchDTO itemPatchDTO,
                                       @RequestPart(name = "logo", required = false) @Image MultipartFile logo,
                                       @RequestPart(name = "pictures", required = false) @ImageArray @MaxFileCount(maxFileAmount = 10) List<MultipartFile> pictures) {

        ItemResponseDTO itemResponseDTO = itemService.patchItem(itemId, itemPatchDTO, logo, pictures);

        return ResponseEntity.ok().body(itemResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete item",
            security = @SecurityRequirement(name = "sessionAuth")
    )
    @DeleteMapping({"/{itemId}"})
    public ResponseEntity<?> deleteItem(@PathVariable int itemId) {

        itemService.deleteItem(itemId);

        return ResponseEntity.noContent().build();
    }
}
