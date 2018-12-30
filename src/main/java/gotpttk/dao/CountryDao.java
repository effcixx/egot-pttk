package gotpttk.dao;

import gotpttk.entities.Country;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CountryDao implements EntityDao<Country, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Country entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Country readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Country.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Country> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Country");
        return query.getResultList();
    }

    @Override
    public void delete(Country entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}
