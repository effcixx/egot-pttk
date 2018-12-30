package gotpttk.dao;

import gotpttk.entities.Point;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointDao implements EntityDao<Point, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Point entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Point readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Point.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Point> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Country");
        return query.getResultList();
    }

    @Override
    public void delete(Point entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

