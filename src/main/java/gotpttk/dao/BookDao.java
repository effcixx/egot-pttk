package gotpttk.dao;

import gotpttk.entities.Book;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookDao implements EntityDao<Book, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Book entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Book readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Book.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Book> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Book");
        return query.getResultList();
    }

    @Override
    public void delete(Book entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

