package gotpttk.dao;

import gotpttk.entities.BookRoute;
import gotpttk.entities.Category;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BookRouteDao implements EntityDao<BookRoute, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(BookRoute entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public BookRoute readById(Integer id) {
        return sessionFactory.getCurrentSession().get(BookRoute.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BookRoute> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from BookRoute");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<BookRoute> getBookRoutesByBookId(int id){
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from BookRoute where book.id = :id");
        query.setParameter("id", id);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<BookRoute> getBookRoutesByBookIdAndCategory(int bookId, Category category,
                                                            Date dateOfScoringLastBadgeWithGivenCategory){
        var session = sessionFactory.getCurrentSession();
        String basicQuery = "from BookRoute where book.id = " + bookId +
                " and currentBadgeCategory.id = " + category.getId();
        if (dateOfScoringLastBadgeWithGivenCategory != null){
            basicQuery += " and dateOfCompletion > \'" + dateOfScoringLastBadgeWithGivenCategory + "\'";
        }
        var query = session.createQuery(basicQuery);
        return query.getResultList();
    }

    @Override
    public void delete(BookRoute entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }
}

