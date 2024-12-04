package com.crawler.backend.repository;

import java.util.Map;

public interface DefaultRepository<T,ID> {
    void save(T element);
    void saveAll(Map<ID,T> elements);
    T find(ID id);
    Map<ID,T> findAll();
    void remove(ID id);
}
