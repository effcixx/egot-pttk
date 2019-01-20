package gotpttk.dao;

import gotpttk.entities.Badge;
import gotpttk.entities.Book;
import gotpttk.entities.Category;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class BadgeDao implements EntityDao<Badge, Integer> {

    @Autowired
    public SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Badge entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public Badge readById(Integer id) {
        return sessionFactory.getCurrentSession().get(Badge.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Badge> readAll() {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Badge");
        return query.getResultList();
    }

    @Override
    public void delete(Badge entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @SuppressWarnings("unchecked")
    public Badge getLastBadgeScored(int userId){
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Badge where owner.id = "+userId);
        List<Badge> badgeList = query.getResultList();
        Optional<Badge> latestBadgeOptional =
                badgeList.stream().max(Comparator.comparing(Badge::getAchievingDate));
        return latestBadgeOptional.orElse(null);
    }

    public Date getDateOfScoringLastBadgeWithGivenCategory(Category category, int userId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("select max(achievingDate) from Badge where owner.id = " + userId + " and category.id = "
                                        + category.getId());
        return (Date) query.getResultList().get(0);
    }

    @SuppressWarnings("unchecked")
    public List<Badge> getAllBadgesScoredByUser(int userId) {
        var session = sessionFactory.getCurrentSession();
        var query = session.createQuery("from Badge where owner.id = " + userId);
        return query.getResultList();
    }
//
//    public Badge getBadgeUnderCompletionOnGivenDate(Date date, int userId) {
//        var session = sessionFactory.getCurrentSession();
//        var query = session.createQuery("from Badge where " + ");
//        return query.getResultList();
//    }
}
