package Onlinestore.controller;

import Onlinestore.dto.item.PostItemDTO;
import Onlinestore.entity.Item;
import Onlinestore.mapper.item.PostItemMapper;
import Onlinestore.repository.ItemRepository;
import Onlinestore.service.ItemService;
import Onlinestore.validation.annotation.item.MaxFileCount;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final PostItemMapper postItemMapper;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<?> postItem(@RequestPart("logo") MultipartFile logo,
                                      @RequestPart("images") @MaxFileCount(max = 10) MultipartFile[] images,
                                      @RequestPart("item") @Valid PostItemDTO postItemDTO) throws URISyntaxException {

        Item item = postItemMapper.postItemDTOToItem(postItemDTO, images.length);

        itemService.saveLogoToFolder(logo, item.getLogoName());
        itemService.saveImagesToFolder(images, item.getImageNames());

        itemRepository.save(item);

        return ResponseEntity.created(new URI("http://localhost:8080/item/" + item.getId())).build();
    }
}
