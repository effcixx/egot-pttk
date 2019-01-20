package gotpttk.service;

import gotpttk.dao.BadgeDao;
import gotpttk.entities.Badge;
import gotpttk.entities.Book;
import gotpttk.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BadgeService {

    @Autowired
    public BadgeDao badgeDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Badge badge){
        badgeDao.saveOrUpdate(badge);
    }

    public Badge readById(int id){
        return badgeDao.readById(id);
    }

    public List<Badge> readAll(){
        return badgeDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Badge badge){
        badgeDao.delete(badge);
    }

    public Badge getLastBadgeScored(int userId){
        var lastBadge = badgeDao.getLastBadgeScored(userId);
        if (lastBadge == null){
            return null;
        }
        int lastBadgeHierarchyLevel = lastBadge.getCategory().getHierarchyLevel();
        return badgeDao.getLastBadgeScored(userId);
    }

    public Date getDateOfScoringLastBadgeWithGivenCategory(Category category, int userId) {
        return badgeDao.getDateOfScoringLastBadgeWithGivenCategory(category, userId);
    }

    public List<Badge> getAllBadgesScoredByUser(int userId) {
        return badgeDao.getAllBadgesScoredByUser(userId);
    }
//
//    public Badge getBadgeUnderCompletionOnGivenDate(Date date, int userId){
//        //Badge badge = badgeDao.getBadgeUnderCompletionOnGivenDate(date, userId);
//        var a =
//    }
}
