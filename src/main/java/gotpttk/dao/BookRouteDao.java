package gotpttk.dao;

import gotpttk.entities.BookRoute;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRouteDao implements EntityDao<BookRoute, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(BookRoute entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public BookRoute readById(Integer id) {
        return sessionFactory.getCurrentSession().get(BookRoute.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BookRoute> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from BookRoute");
        return query.getResultList();
    }

    @Override
    public void delete(BookRoute entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

