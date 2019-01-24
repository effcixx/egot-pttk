package gotpttk.service;

import gotpttk.dao.BadgeDao;
import gotpttk.entities.Badge;
import gotpttk.entities.Book;
import gotpttk.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        return badgeDao.getLastBadgeScored(userId);
    }

    public Date getDateOfScoringLastBadgeWithGivenCategory(Category category, int userId) {
        return badgeDao.getDateOfScoringLastBadgeWithGivenCategory(category, userId);
    }

    public List<Badge> getAllBadgesScoredByUser(int userId) {
        return badgeDao.getAllBadgesScoredByUser(userId);
    }

    public java.util.Date[] getPeriodOfScoringGivenBadge(List<Badge> userBadges, Badge badge){
        var userBadgesSorted = userBadges.stream()
                .sorted(Comparator.comparing(Badge::getAchievingDate))
                .collect(Collectors.toList());
        boolean found = false;
        java.util.Date[] dates = null;
        for (int i=0; i<userBadgesSorted.size() && !found; i++){
            if (userBadgesSorted.get(i).equals(badge)){
                found = true;
                if (i == 0){
                    dates =  new java.util.Date[]{new java.util.Date(0), badge.getAchievingDate()};
                }
                else{
                    dates= new java.util.Date[]{userBadgesSorted.get(i-1).getAchievingDate(),
                            badge.getAchievingDate()};
                }
            }
        }
        return dates;
    }

}
