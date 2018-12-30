package gotpttk.dao;

import gotpttk.entities.Excursion;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExcursionDao implements EntityDao<Excursion, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Excursion entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Excursion readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Excursion.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Excursion> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Excursion ");
        return query.getResultList();
    }

    @Override
    public void delete(Excursion entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

