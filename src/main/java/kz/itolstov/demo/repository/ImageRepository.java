package kz.itolstov.demo.repository;

import kz.itolstov.demo.model.ItemImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ItemImageInfo, Long> {
}
