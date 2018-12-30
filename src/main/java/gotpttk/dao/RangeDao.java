package gotpttk.dao;

import gotpttk.entities.Range;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RangeDao implements EntityDao<Range, Integer> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Range entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Range readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Range.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Range> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Range");
        return query.getResultList();
    }

    @Override
    public void delete(Range entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}
