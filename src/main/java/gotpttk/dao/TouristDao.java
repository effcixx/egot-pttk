package gotpttk.dao;

import gotpttk.entities.Tourist;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TouristDao implements EntityDao<Tourist, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Tourist entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Tourist readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Tourist.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Tourist> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Tourist");
        return query.getResultList();
    }

    @Override
    public void delete(Tourist entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

