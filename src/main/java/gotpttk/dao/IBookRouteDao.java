package gotpttk.dao;

import gotpttk.entities.BookRoute;

import java.util.List;

public interface IBookRouteDao extends EntityDao<BookRoute, Integer> {
    List<BookRoute> getBookRoutesByBookId(int id);
}
