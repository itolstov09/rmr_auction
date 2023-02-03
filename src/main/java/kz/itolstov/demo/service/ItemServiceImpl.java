package kz.itolstov.demo.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import kz.itolstov.demo.exception.AuctionException;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;


    @Override
    public Page<Item> findActiveItems(Integer page, Integer size) {
        return itemRepository.findByStatus(PageRequest.of(page, size), Item.Status.ACTIVE);
    }

//    @Override
//    public Page<Item> findByUsername(String username, Integer page, Integer size) {
//        return itemRepository.findByOwnerEmail(PageRequest.of(page, size), username);
//    }

    @Override
    public Page<Item> findByOwnerId(Long id, Integer page, Integer size) {
        return itemRepository.findByOwnerId(PageRequest.of(page, size), id);
    }

    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new AuctionException("Item not found"));
    }


}
