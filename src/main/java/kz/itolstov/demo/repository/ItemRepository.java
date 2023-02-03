package kz.itolstov.demo.repository;

import kz.itolstov.demo.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByStatus(PageRequest of, Item.Status status);

    // todo пока не нужен. возможно стоит удалить
    Page<Item> findByOwnerEmail(PageRequest of, String email);

    Page<Item> findByOwnerId(PageRequest of, Long id);
}
