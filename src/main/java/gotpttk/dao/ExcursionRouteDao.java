package gotpttk.dao;

import gotpttk.entities.ExcursionRoute;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExcursionRouteDao implements EntityDao<ExcursionRoute, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(ExcursionRoute entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public ExcursionRoute readById(Integer id) {
        return sessionFactory.getCurrentSession().get(ExcursionRoute.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExcursionRoute> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Country");
        return query.getResultList();
    }

    @Override
    public void delete(ExcursionRoute entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

