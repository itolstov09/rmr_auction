package kz.itolstov.demo.service;

import kz.itolstov.demo.model.Item;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface ItemService extends AuctionBaseService<Item>{

    Page<Item> findActiveItems(Integer page, Integer size);

//    Page<Item> findByUsername(String username, Integer page, Integer size);

    Page<Item> findByOwnerId(Long id, Integer page, Integer size);

    Set<Long> getActiveItemIds();

    List<Item> getItemsNeedToDiscard();
}
