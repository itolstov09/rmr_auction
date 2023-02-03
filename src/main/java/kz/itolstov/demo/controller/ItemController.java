package kz.itolstov.demo.controller;

import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping
    public ResponseEntity<Page<Item>> findActiveItems( @RequestParam Integer page, @RequestParam Integer size ) {
        return ResponseEntity.ok(itemService.findActiveItems(page, size));
    }


}
