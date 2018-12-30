package gotpttk.dao;

import java.util.List;

public interface EntityDao<T, K> {
    void saveOrUpdate(T entity);
    T readById(K id);
    List<T> readAll();
    void delete(T entity);
}
