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

import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;


/**
 * Klasa zawiera logikę biznesową związaną z obiektami reprezentującymi trasy w książeczce.
 */
@Service
@Transactional
public class BookRouteService {

    public BookRouteDao bookRouteDao;

    public BadgeDao badgeDao;

    public BookService bookService;

    public CategoryService categoryService;

    public BadgeService badgeService;

    public ValidatorManager validator;

    @Autowired
    public void setBookRouteDao(BookRouteDao bookRouteDao) {
        this.bookRouteDao = bookRouteDao;
    }

    @Autowired
    public void setBadgeDao(BadgeDao badgeDao) {
        this.badgeDao = badgeDao;
    }

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setBadgeService(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @Autowired
    public void setValidator(ValidatorManager validator) {
        this.validator = validator;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(BookRoute bookRoute) {
        updatePointsBasedOnBadgeHistory(bookRoute);
        bookRouteDao.saveOrUpdate(bookRoute);
        validator.manageValidation(bookRoute.getBook());
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

    List<BookRoute> readUserRoutes(int userId){
        int bookId = bookService.getBookWithUserId(userId).getId();
        return bookRouteDao.getBookRoutesByBookId(bookId);
    }

    private void updatePointsBasedOnBadgeHistory(BookRoute bookRouteToAdd) {
        int userId = bookRouteToAdd.getBook().getOwner().getId();
        int pointsAwardedToCurrentBookRoute;
        var sameRoutesAlreadyCompleted = getSameRoutesAlreadyCompletedUnderCurrentBadge(userId, bookRouteToAdd);
        boolean isRouteAlreadyCompleted = !sameRoutesAlreadyCompleted.isEmpty();
        var lastBadgeScored = badgeService.getLastBadgeScored(userId);
        boolean periodUnderAlreadyAwardedBadge = lastBadgeScored != null
                && bookRouteToAdd.getDateOfCompletion().getTime()
                            < lastBadgeScored.getAchievingDate().getTime();
        /// todo jako nadwyzka
        if (periodUnderAlreadyAwardedBadge){
            bookRouteToAdd.setPointsAwarded(0);
            return;
        }

        if (isRouteAlreadyCompleted){
            setPointsOfSameRoutesCompletedAfterGivenRoute(bookRouteToAdd, sameRoutesAlreadyCompleted);
            pointsAwardedToCurrentBookRoute = getPointsAwardedToCurrentBookRouteBasedOnHistory
                    (bookRouteToAdd);
        }
        else{
            pointsAwardedToCurrentBookRoute = getPointsBasedOnDirection(bookRouteToAdd);
        }
        bookRouteToAdd.setPointsAwarded(pointsAwardedToCurrentBookRoute);
    }

    private List<BookRoute> getSameRoutesAlreadyCompletedUnderCurrentBadge(int userId, BookRoute bookRouteToAdd){
        return readRoutesUnderCurrentBadge(userId)
                .stream()
                .filter(route -> route.getRoute().equals(bookRouteToAdd.getRoute())
                        && route.getIsFromStartToEnd() == bookRouteToAdd.getIsFromStartToEnd())
                .collect(Collectors.toList());
    }

    public List<BookRoute> readRoutesUnderCurrentBadge(int userId){
        int bookId = bookService.getBookWithUserId(userId).getId();
        var category = categoryService.getCategoryOfCurrentBadge(userId);
        var dateOfScoringLastBadgeWithGivenCategory =
                badgeService.getDateOfScoringLastBadgeWithGivenCategory(category, userId);
        return bookRouteDao.getBookRoutesByBookIdAndCategory
                (bookId, category, dateOfScoringLastBadgeWithGivenCategory);
    }

    public List<BookRoute> readRoutesCompletedUnderGivenBadge(Badge badge, int userId){
        var userBadges = badgeService.getAllBadgesScoredByUser(userId);
        var period = badgeService.getPeriodOfScoringGivenBadge(userBadges, badge);
        var userBookRoutes = readUserRoutes(userId);
        return userBookRoutes.stream()
                .filter(bookRoute -> bookRoute.getCurrentBadgeCategory().equals(badge.getCategory())
                        && bookRoute.getDateOfCompletion().getTime() >= period[0].getTime()
                        && bookRoute.getDateOfCompletion().getTime() <= period[1].getTime())
                .collect(Collectors.toList());
    }

    public void setPointsOfSameRoutesCompletedAfterGivenRoute(BookRoute bookRouteToAdd, List<BookRoute> sameRoutesAlreadyCompleted) {
        var sameRoutesCompletedAfter = sameRoutesAlreadyCompleted
                .stream()
                .filter(b -> b.getDateOfCompletion().getTime()
                        >= bookRouteToAdd.getDateOfCompletion().getTime());
        sameRoutesCompletedAfter.forEach(bookRoute -> bookRoute.setPointsAwarded(0));
    }

    public int getPointsAwardedToCurrentBookRouteBasedOnHistory
            (BookRoute bookRouteToAdd) {
        int userId = bookRouteToAdd.getBook().getOwner().getId();
        int pointsAwardedToCurrentBookRoute = getPointsBasedOnDirection(bookRouteToAdd);
        var sameRoutesAlreadyCompleted = getSameRoutesAlreadyCompletedUnderCurrentBadge(userId, bookRouteToAdd);

        boolean areSameRoutesCompletedBefore = sameRoutesAlreadyCompleted.stream()
                .anyMatch(bookRoute -> bookRoute.getDateOfCompletion().getTime()
                                    < bookRouteToAdd.getDateOfCompletion().getTime());
        if (areSameRoutesCompletedBefore){
            pointsAwardedToCurrentBookRoute = 0;
        }
        return pointsAwardedToCurrentBookRoute;
    }

    public int getPointsBasedOnDirection(BookRoute bookRoute){
        return bookRoute.getIsFromStartToEnd() ?
                bookRoute.getRoute().getPointsStartToEnd() :
                bookRoute.getRoute().getPointsEndToStart();
    }

    public TreeMap<Badge, List<BookRoute>> getBookRoutesAndRespectiveBadges(int userId){
        var badgesScored = badgeService.getAllBadgesScoredByUser(userId).stream()
                .sorted((b1, b2) -> b2.getAchievingDate().compareTo(b1.getAchievingDate()))
                .collect(Collectors.toList());

        List<BookRoute> bookRoutes = readUserRoutes(userId).stream()
                .sorted(new BookRouteService.BookRouteComparator())
                .collect(Collectors.toList());

        var badgeRouteMap = new TreeMap<Badge, List<BookRoute>>(
                (b1, b2) -> b2.getAchievingDate().compareTo(b1.getAchievingDate())
        );

        addScoredBadgesAndTheirRespectiveRoutes(badgesScored, bookRoutes, badgeRouteMap);

        updateBadgeRouteMapWithCurrentBadgeRoutes(userId, bookRoutes, badgeRouteMap);

        return badgeRouteMap;
    }

    public void updateBadgeRouteMapWithCurrentBadgeRoutes(int userId, List<BookRoute> bookRoutes,
                                                           TreeMap<Badge, List<BookRoute>> badgeRouteMap) {
        List<BookRoute> currentBookRoutes = getBookRoutesCompletedUnderCurrentBadge(userId, bookRoutes);
        var badge = new Badge();
        badge.setAchievingDate(new Date(new java.util.Date().getTime()));
        badge.setCategory(categoryService.getCategoryOfCurrentBadge(userId));
        badgeRouteMap.put(badge, currentBookRoutes);
    }

    public List<BookRoute> getBookRoutesCompletedUnderCurrentBadge(int userId, List<BookRoute> bookRoutes) {
        List<BookRoute> currentBookRoutes;
        var lastBadgeScored = badgeService.getLastBadgeScored(userId);
        if (lastBadgeScored == null){
            currentBookRoutes = bookRoutes;
        }
        else{
            currentBookRoutes = bookRoutes.stream().filter(bookRoute ->
                    bookRoute.getDateOfCompletion().getTime() >= lastBadgeScored.getAchievingDate().getTime()
                            && !bookRoute.getCurrentBadgeCategory().equals(lastBadgeScored.getCategory()))
                    .sorted(new BookRouteService.BookRouteComparator())
                    .collect(Collectors.toList());
        }
        return currentBookRoutes;
    }

    public void addScoredBadgesAndTheirRespectiveRoutes(List<Badge> badgesScored,
                                                         List<BookRoute> bookRoutes,
                                                         TreeMap<Badge, List<BookRoute>> badgeRouteMap) {
        for (var badge : badgesScored){
            var badgeScoringPeriod = badgeService.getPeriodOfScoringGivenBadge(badgesScored, badge);
            var listOfRoutes = bookRoutes.stream()
                    .filter(bookRoute ->
                            bookRoute.getCurrentBadgeCategory().equals(badge.getCategory())
                                    && bookRoute.getDateOfCompletion().getTime() >= badgeScoringPeriod[0].getTime()
                                    && bookRoute.getDateOfCompletion().getTime() <= badgeScoringPeriod[1].getTime()
                    )
                    .sorted(new BookRouteComparator())
                    .collect(Collectors.toList());
            badgeRouteMap.put(badge, listOfRoutes);
        }
    }

    public static class BookRouteComparator implements Comparator<BookRoute> {
        @Override
        public int compare(BookRoute firstBookRoute, BookRoute secondBookRoute) {
            boolean areOnSameDay = firstBookRoute.getDateOfCompletion()
                    .equals(secondBookRoute.getDateOfCompletion());
            int result;
            if (areOnSameDay){
                if (firstBookRoute.getRoute().equals(secondBookRoute.getRoute())){
                    result = firstBookRoute.getPointsAwarded() - secondBookRoute.getPointsAwarded();
                }
                else{
                    result = firstBookRoute.getId() - secondBookRoute.getId();
                }
            }
            else{
                result = secondBookRoute.getDateOfCompletion()
                        .compareTo(firstBookRoute.getDateOfCompletion());
            }
            return result;
        }
    }


}

