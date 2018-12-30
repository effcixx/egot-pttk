package gotpttk.dao;

import gotpttk.entities.Region;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegionDao implements EntityDao<Region, Integer> {
    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Region entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Region readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Region.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Region> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Region");
        return query.getResultList();
    }

    @Override
    public void delete(Region entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}
