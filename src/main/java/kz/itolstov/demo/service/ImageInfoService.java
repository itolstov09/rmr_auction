package kz.itolstov.demo.service;

import kz.itolstov.demo.model.ItemImageInfo;
import kz.itolstov.demo.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageInfoService {

    private final ImageRepository repository;

    public ItemImageInfo save(ItemImageInfo image) {
        return repository.save(image);
    }

}
