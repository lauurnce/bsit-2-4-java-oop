package dao;

import java.util.List;

/** Generic CRUD contract (abstraction) implemented by every entity DAO. */
public interface Crud<T> {
    void add(T item) throws Exception;
    List<T> getAll() throws Exception;
    List<T> search(String keyword) throws Exception;
    void update(T item) throws Exception;
    void delete(int id) throws Exception;
}
