package gotpttk.dao;

import gotpttk.entities.Admin;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminDao implements EntityDao<Admin, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Admin entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Admin readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Admin.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Admin> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Admin ");
        return query.getResultList();
    }

    @Override
    public void delete(Admin entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}
