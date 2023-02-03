package kz.itolstov.demo.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.service.ItemService;
import kz.itolstov.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("{id}/items")
    public ResponseEntity<Page<Item>> findItemsById(
            @PathVariable Long id,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        return ResponseEntity.ok(itemService.findByOwnerId(id, page, size));
    }

}
