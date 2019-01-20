package gotpttk.service;

import gotpttk.dao.BadgeDao;
import gotpttk.dao.BookRouteDao;
import gotpttk.entities.Badge;
import gotpttk.entities.BookRoute;
import gotpttk.validators.ValidatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookRouteService {

    @Autowired
    public BookRouteDao bookRouteDao;

    @Autowired
    public BadgeDao badgeDao;

    @Autowired
    public BookService bookService;

    @Autowired
    public CategoryService categoryService;

    @Autowired
    public BadgeService badgeService;

    @Autowired
    public ValidatorManager validator;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(BookRoute bookRoute) {
        updatePointsBasedOnBadgeHistory(bookRoute);
        System.out.println("Ostateczna trasa: " + bookRoute);
        bookRouteDao.saveOrUpdate(bookRoute);
        System.out.println("after saving");
        validator.manageValidation(bookRoute.getBook());
        System.out.println("after validation");
    }

    public BookRoute readById(int id){
        return bookRouteDao.readById(id);
    }

    public List<BookRoute> readAll(){
        return bookRouteDao.readAll();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(BookRoute bookRoute){
        bookRouteDao.delete(bookRoute);
    }

    public List<BookRoute> readUserRoutes(int userId){
        int bookId = bookService.getBookWithUserId(userId).getId();
        return bookRouteDao.getBookRoutesByBookId(bookId);
    }

    public List<BookRoute> readRoutesUnderCurrentBadge(int userId){
        int bookId = bookService.getBookWithUserId(userId).getId();
        var category = categoryService.getCategoryOfCurrentBadge(userId);
        var dateOfScoringLastBadgeWithGivenCategory =
                badgeService.getDateOfScoringLastBadgeWithGivenCategory(category, userId);
        return bookRouteDao.getBookRoutesByBookIdAndCategory
                (bookId, category, dateOfScoringLastBadgeWithGivenCategory);
    }

    private void updatePointsBasedOnBadgeHistory(BookRoute bookRouteToAdd) {
        int userId = bookRouteToAdd.getBook().getOwner().getId();
        int pointsAwardedToCurrentBookRoute = getPointsBasedOnDirection(bookRouteToAdd);
        var sameRoutesAlreadyCompleted = getSameRoutesAlreadyCompletedUnderCurrentBadge(userId, bookRouteToAdd);
        boolean isRouteAlreadyCompleted = !sameRoutesAlreadyCompleted.isEmpty();
        var lastBadgeScored = badgeService.getLastBadgeScored(userId);
        if (lastBadgeScored != null && bookRouteToAdd.getDateOfCompletion().getTime()
                < lastBadgeScored.getAchievingDate().getTime()){
            bookRouteToAdd.setPointsAwarded(0);
            return;
        }

        if (isRouteAlreadyCompleted){
            var sameRoutesCompletedAfter = sameRoutesAlreadyCompleted
                    .stream()
                    .filter(b -> b.getDateOfCompletion().getTime()
                                        >= bookRouteToAdd.getDateOfCompletion().getTime());
            sameRoutesCompletedAfter.forEach(bookRoute -> bookRoute.setPointsAwarded(0));
            boolean areSameRoutesCompletedBefore = sameRoutesAlreadyCompleted.stream()
                    .anyMatch(bookRoute -> bookRoute.getDateOfCompletion().getTime()
                                        < bookRouteToAdd.getDateOfCompletion().getTime());
            if (areSameRoutesCompletedBefore){
                pointsAwardedToCurrentBookRoute = 0;
            }
        }
        else{
            pointsAwardedToCurrentBookRoute = getPointsBasedOnDirection(bookRouteToAdd);
        }
        bookRouteToAdd.setPointsAwarded(pointsAwardedToCurrentBookRoute);
        System.out.println("Updated");
    }

    private List<BookRoute> getSameRoutesAlreadyCompletedUnderCurrentBadge(int userId, BookRoute bookRouteToAdd){
        return readRoutesUnderCurrentBadge(userId)
                .stream()
                .filter(route -> route.getRoute().equals(bookRouteToAdd.getRoute())
                        && route.getIsFromStartToEnd() == bookRouteToAdd.getIsFromStartToEnd())
                .collect(Collectors.toList());
    }

    private int getPointsBasedOnDirection(BookRoute bookRoute){
        return bookRoute.getIsFromStartToEnd() ?
                bookRoute.getRoute().getPointsStartToEnd() :
                bookRoute.getRoute().getPointsEndToStart();
    }



}

