package gotpttk.service;

import gotpttk.dao.CategoryDao;
import gotpttk.entities.Badge;
import gotpttk.entities.Category;
import gotpttk.dao.EntityDao;
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
public class CategoryService {

    @Autowired
    public BadgeService badgeService;

    @Autowired
    public CategoryDao categoryDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Category category){
        categoryDao.saveOrUpdate(category);
    }

    public Category readById(int id){
        return categoryDao.readById(id);
    }

    public List<Category> readAll(){
        return categoryDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Category category){
        categoryDao.delete(category);
    }

    public Category getCategoryOfCurrentBadge(int userId){
        var mostRecentBadge = badgeService.getLastBadgeScored(userId);
        var categories = categoryDao.readAll();
        if (mostRecentBadge == null){
            return categories.stream().min(Comparator.comparingInt(Category::getHierarchyLevel)).get();
        }
        int highestHierarchyLevel = getHighestHierarchyLevel(categories);
        System.out.println("Highest hierarchy: " + highestHierarchyLevel);
        int lastBadgeHierarchyLevel = mostRecentBadge.getCategory().getHierarchyLevel();
        System.out.println("Last badge hierarchy: " + lastBadgeHierarchyLevel);
        if (lastBadgeHierarchyLevel == highestHierarchyLevel){
            // co tutaj?
            return categories.stream().min(Comparator.comparingInt(Category::getHierarchyLevel)).get();
        }
        return categories.stream().filter(category -> category.getHierarchyLevel() == lastBadgeHierarchyLevel+1)
                .findFirst().get();
    }

    private int getHighestHierarchyLevel(List<Category> categories) {
        var highestHierarchyLevelOptional =
                categories.stream().max(Comparator.comparingInt(Category::getHierarchyLevel));
        return highestHierarchyLevelOptional.map(Category::getHierarchyLevel).orElse(0);
    }

    /// todo poprawionko
    public Category getCategoryOfBadgeUnderCompletionOnGivenDate(Date date, int userId){
        List<Badge> userBadges = badgeService.getAllBadgesScoredByUser(userId)
                        .stream()
                        .sorted(Comparator.comparing((Badge b) -> b.getCategory().getHierarchyLevel())
                                .thenComparing(Badge::getAchievingDate))
                        .collect(Collectors.toList());
        boolean found = false;
        Category category = categoryDao.getCategoryWithLowestHierarchyLevel();
        if (userBadges.isEmpty()){
            return category;
        }
        for (int i=0; i<userBadges.size() && !found; i++){
            var currentBadge = userBadges.get(i);
            if (currentBadge.getAchievingDate().getTime() > date.getTime()){
                category = currentBadge.getCategory();
                found = true;
            }
        }
        if (!found){
            category = getCategoryOfCurrentBadge(userId);
        }
        return category;
    }

}
