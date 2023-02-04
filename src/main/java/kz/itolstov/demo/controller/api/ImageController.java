package kz.itolstov.demo.controller.api;

import kz.itolstov.demo.model.ItemImageInfo;
import kz.itolstov.demo.service.ImageInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/images")
@RequiredArgsConstructor
//todo перевести на ResponseEntity
public class ImageController {

    private final ImageInfoService imageInfoService;


    @PostMapping
    public ItemImageInfo save(@RequestBody ItemImageInfo image) {
        return imageInfoService.save(image);
    }

}
