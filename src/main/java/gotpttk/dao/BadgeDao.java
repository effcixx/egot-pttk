package gotpttk.dao;

import gotpttk.entities.Badge;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BadgeDao implements EntityDao<Badge, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Badge entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Badge readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Badge.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Badge> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Badge");
        return query.getResultList();
    }

    @Override
    public void delete(Badge entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}
