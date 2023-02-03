package kz.itolstov.demo.service;

import kz.itolstov.demo.model.BaseEntity;

public interface AuctionBaseService<T extends BaseEntity> {

    T save(T object);

    T findById(Long id);
}
