package kz.itolstov.demo.repository;

import kz.itolstov.demo.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByStatus(PageRequest of, Item.Status status);

    Page<Item> findByOwnerId(PageRequest of, Long id);

    @Query(
    """
    SELECT item.id
    FROM Item item
    WHERE item.status=0
    """ )
    Set<Long> getActiveItemIds();


    // todo вопросик про пояса. стоит ли их тут учитывать. либо для поля, либо для now
    @Query(
    """
    SELECT item
    FROM Item item
    WHERE item.status=0 AND item.auctionEndsAt < now()
    """
    )
    List<Item> getItemsNeedToDiscard();
}
