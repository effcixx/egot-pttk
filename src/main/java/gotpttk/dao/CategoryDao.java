package gotpttk.dao;

import gotpttk.entities.Category;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDao implements EntityDao<Category, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Category entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Category readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Category.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Category> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Category ");
        return query.getResultList();
    }

    @Override
    public void delete(Category entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

