package kz.itolstov.demo.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.persistence.criteria.CriteriaBuilder;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.model.User;
import kz.itolstov.demo.service.ItemService;
import kz.itolstov.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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

    @PostMapping
    public ResponseEntity<User> save(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "valid user",
                                    ref = "#/components/examples/user-POST-201-ex1"
                            )
                    } ) )
            User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.save(user));

    }

}
