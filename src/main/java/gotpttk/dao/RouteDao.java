package gotpttk.dao;

import gotpttk.entities.Route;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RouteDao implements EntityDao<Route, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Route entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Route readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Route.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Route> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Route");
        return query.getResultList();
    }

    /// TODO ZMIENIC
    @SuppressWarnings("unchecked")
    public List<Route> readAllPublic(){
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Route where routeOwner = null");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Route> readRoutesDefinedByUser(int userId){
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Route where routeOwner.id = "+userId);
        //query.setParameter(userId, "userId");
        return query.getResultList();
    }

    @Override
    public void delete(Route entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}
