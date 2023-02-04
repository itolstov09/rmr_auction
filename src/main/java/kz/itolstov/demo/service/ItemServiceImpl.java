package kz.itolstov.demo.service;

import kz.itolstov.demo.exception.AuctionException;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;


    @Override
    public Page<Item> findActiveItems(Integer page, Integer size) {
        return itemRepository.findByStatus(PageRequest.of(page, size), Item.Status.ACTIVE);
    }


    @Override
    public Page<Item> findByOwnerId(Long id, Integer page, Integer size) {
        return itemRepository.findByOwnerId(PageRequest.of(page, size), id);
    }

    @Override
    public Set<Long> getActiveItemIds() {
        return itemRepository.getActiveItemIds();
    }

    @Override
    public List<Item> getItemsNeedToDiscard() {
        return itemRepository.getItemsNeedToDiscard();
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
