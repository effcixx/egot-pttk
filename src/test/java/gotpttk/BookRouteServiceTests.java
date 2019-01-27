package gotpttk;

import gotpttk.dao.BookRouteDao;
import gotpttk.entities.*;
import gotpttk.service.BadgeService;
import gotpttk.service.BookRouteService;
import gotpttk.service.BookService;
import gotpttk.service.CategoryService;
import org.joda.time.DateTimeComparator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.*;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BookRouteServiceTests {

    @Mock
    private BadgeService badgeService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookRouteDao bookRouteDao;

    @Mock
    private BookService bookService;

    private BookRouteService bookRouteService;

    private BookRouteService.BookRouteComparator bookRouteComparator;

    private List<BookRoute> oldBookRoutes;
    private List<BookRoute> newerBookRoutes;
    private List<Badge> badgesScored;
    private Comparator<? super Badge> basicBadgeComparator;

    @Before
    public void setUpFields() {
        bookRouteService = new BookRouteService();
        bookRouteService.setBadgeService(badgeService);
        bookRouteService.setCategoryService(categoryService);
        bookRouteService.setBookRouteDao(bookRouteDao);
        bookRouteService.setBookService(bookService);
        bookRouteComparator = new BookRouteService.BookRouteComparator();
        basicBadgeComparator = (b1, b2) -> b2.getAchievingDate().compareTo(b1.getAchievingDate());
        basicBadgeComparator = (Comparator<Badge>) (o1, o2)
                -> DateTimeComparator.getDateOnlyInstance().compare(o1.getAchievingDate(), o2.getAchievingDate());

        setUpBookRoutes();
    }

    private void setUpBookRoutes() {
        int severalDays = 360000000*2;
        var pastDate = new Date(new java.util.Date().getTime()-severalDays);
        var todayDate = new Date(new java.util.Date().getTime());
        var pastPastDate = new Date(pastDate.getTime() - severalDays);

        var previousCategory = new Category(1, "Test_1");
        var nextCategory = new Category(2, "Test_2");

        badgesScored = new ArrayList<>();
        badgesScored.add(new Badge(pastDate, previousCategory, new Tourist()));
        badgesScored.add(new Badge(todayDate, nextCategory, new Tourist()));
        oldBookRoutes = new ArrayList<>();
        var firstRoute = new Route(1, 1, new Point(), new Point(), null);
        var firstRouteDate = new Date(pastDate.getTime() - severalDays);
        oldBookRoutes.add(new BookRoute(true, firstRouteDate, previousCategory, firstRoute, new Book()));

        newerBookRoutes = new ArrayList<>();
        var newRoute = new Route(1, 2, new Point(), new Point(), null);
        newerBookRoutes.add(new BookRoute(true, todayDate, nextCategory, newRoute, new Book()));

    }

    @Test
    public void getBookRoutesCompletedUnderCurrentBadge_badgesNotEmpty() {

        var randomMiliseconds = 360000000;
        var date = new Date(new java.util.Date(randomMiliseconds / 100).getTime());
        Mockito.when(badgeService.getLastBadgeScored(anyInt()))
                .thenReturn(new Badge(date, new Category(), new Tourist()));
        var allRoutes = new ArrayList<BookRoute>();
        var bookRouteCategory = new Category(1, "Test_1");
        allRoutes.add(new BookRoute(true, new Date(date.getTime()), bookRouteCategory, new Route(), new Book()));
        var routes = bookRouteService
                .getBookRoutesCompletedUnderCurrentBadge(1, allRoutes);
        assertEquals(allRoutes, routes);
    }

    @Test
    public void getBookRoutesCompletedUnderCurrentBadge_noBadges() {
        var randomMiliseconds = 360000000;
        var date = new Date(new java.util.Date(randomMiliseconds / 100).getTime());
        Mockito.when(badgeService.getLastBadgeScored(anyInt()))
                .thenReturn(null);
        var allRoutes = new ArrayList<BookRoute>();
        var bookRouteCategory = new Category(1, "Test_1");
        allRoutes.add(new BookRoute(true, new Date(date.getTime()), bookRouteCategory, new Route(), new Book()));
        allRoutes.add(new BookRoute(false, new Date(date.getTime()/1000), bookRouteCategory, new Route(), new Book()));
        var routes = bookRouteService
                .getBookRoutesCompletedUnderCurrentBadge(1, allRoutes);
        assertEquals(allRoutes, routes);
    }

    @Test
    public void bookRouteComparator_differentDay(){
        int severalDays = 360000000*2;
        var earlierDay = new Date(new java.util.Date().getTime()-severalDays);
        var currentDay = new Date(new java.util.Date().getTime());

        var earlierBookRoute = new BookRoute();
        earlierBookRoute.setDateOfCompletion(earlierDay);
        var laterBookRoute = new BookRoute();
        laterBookRoute.setDateOfCompletion(currentDay);

        int result = bookRouteComparator.compare(earlierBookRoute, laterBookRoute);
        assertTrue(result > 0);
    }

    @Test
    public void bookRouteComparator_sameDaySameRoutes(){
        var currentDay = new Date(new java.util.Date().getTime());
        int morePoints = 2;
        var bookRouteWithZeroPoints = new BookRoute();
        var route = new Route();
        bookRouteWithZeroPoints.setPointsAwarded(0);
        bookRouteWithZeroPoints.setDateOfCompletion(currentDay);
        bookRouteWithZeroPoints.setRoute(route);
        var bookRouteWithMorePoints = new BookRoute();
        bookRouteWithMorePoints.setPointsAwarded(morePoints);
        bookRouteWithMorePoints.setDateOfCompletion(currentDay);
        bookRouteWithMorePoints.setRoute(route);

        int result = bookRouteComparator
                .compare(bookRouteWithZeroPoints, bookRouteWithMorePoints);
        assertTrue(result < 0);
    }

    @Test
    public void bookRouteComparator_sameDayDifferentRoutes(){
        var firstRoute = new Route(1, 2, new Point(), new Point(), null);
        var secondRoute = new Route(5, 5, new Point(), new Point(), null);

        var currentDay = new Date(new java.util.Date().getTime());
        var previouslyAddedBookRoute = new BookRoute();
        previouslyAddedBookRoute.setId(10);
        previouslyAddedBookRoute.setRoute(firstRoute);
        previouslyAddedBookRoute.setDateOfCompletion(currentDay);

        var laterAddedBookRoute = new BookRoute();
        laterAddedBookRoute.setId(11);
        laterAddedBookRoute.setRoute(secondRoute);
        laterAddedBookRoute.setDateOfCompletion(currentDay);

        int result = bookRouteComparator
                .compare(previouslyAddedBookRoute, laterAddedBookRoute);

        assertTrue(result < 0);
    }

    @Test
    public void addScoredBadgesAndTheirRespectiveRoutes_test(){
        int severalDays = 360000000*2;
        var pastDate = new Date(new java.util.Date().getTime()-severalDays);
        var todayDate = new Date(new java.util.Date().getTime());
        var pastPastDate = new Date(pastDate.getTime() - severalDays);

        var map = new TreeMap<Badge, List<BookRoute>>(basicBadgeComparator);
        var emptyMap = new TreeMap<Badge, List<BookRoute>>(basicBadgeComparator);
        Mockito.when(badgeService.getPeriodOfScoringGivenBadge(anyList(), eq(badgesScored.get(0))))
                .thenReturn(new java.util.Date[]{pastPastDate, pastDate});
        Mockito.when(badgeService.getPeriodOfScoringGivenBadge(anyList(), eq(badgesScored.get(1))))
                .thenReturn(new java.util.Date[]{pastDate, todayDate});

        map.put(badgesScored.get(0), oldBookRoutes);
        map.put(badgesScored.get(1), newerBookRoutes);

        var allBookRoutes = new ArrayList<BookRoute>();
        allBookRoutes.addAll(oldBookRoutes);
        allBookRoutes.addAll(newerBookRoutes);

        bookRouteService.addScoredBadgesAndTheirRespectiveRoutes(badgesScored, allBookRoutes, emptyMap);

        assertEquals(map, emptyMap);
    }

    @Test
    public void updateBadgeRouteMapWithCurrentBadgeRoutes_test(){
        Mockito.when(badgeService.getLastBadgeScored(anyInt()))
                .thenReturn(badgesScored.get(0));
        var category = new Category(2, "Test_2");
        Mockito.when(categoryService.getCategoryOfCurrentBadge(anyInt()))
                .thenReturn(category);
        var emptyMap = new TreeMap<Badge, List<BookRoute>>(basicBadgeComparator);
        emptyMap.put(badgesScored.get(0), oldBookRoutes);

        var map = new TreeMap<Badge, List<BookRoute>>(basicBadgeComparator);
        map.put(badgesScored.get(0), oldBookRoutes);

        var badge = new Badge();
        badge.setAchievingDate(new Date(new java.util.Date().getTime()));
        badge.setCategory(category);
        map.put(badge, newerBookRoutes);


        bookRouteService.updateBadgeRouteMapWithCurrentBadgeRoutes(1, newerBookRoutes, emptyMap);

        assertEquals(emptyMap, map);
    }


    @Test
    public void getPointsBasedOnDirection_startToEnd(){
        int pointsStartEnd = 10;
        var route = new Route();
        route.setPointsStartToEnd(pointsStartEnd);
        var bookRoute = new BookRoute();
        bookRoute.setIsFromStartToEnd(true);
        bookRoute.setRoute(route);
        int points = bookRouteService.getPointsBasedOnDirection(bookRoute);
        assertEquals(pointsStartEnd, points);
    }

    @Test
    public void getPointsBasedOnDirection_endToStart(){
        int pointsEndStart = 5;
        var route = new Route();
        route.setPointsEndToStart(pointsEndStart);
        var bookRoute = new BookRoute();
        bookRoute.setIsFromStartToEnd(false);
        bookRoute.setRoute(route);
        int points = bookRouteService.getPointsBasedOnDirection(bookRoute);
        assertEquals(pointsEndStart, points);
    }

    @Test
    public void getPointsAwardedToCurrentBookRouteBasedOnHistory_sameRouteCompletedBefore(){
        int severalDays = 180000000;
        int pointsStartEnd = 2;
        int pointsEndStart = 5;

        var currentCategory = new Category(2, "Test_2");

        var book = new Book();
        book.setOwner(new Tourist());

        var route = new Route(pointsStartEnd, pointsEndStart, new Point(), new Point(), null);

        var bookRouteOne = new BookRoute(true,
                new Date(new java.util.Date().getTime()-severalDays), currentCategory, route, book);
        var sameRoute = new BookRoute(true,
                new Date(new java.util.Date().getTime()), currentCategory, route, book);

        var routesUnderBadge = new ArrayList<>(newerBookRoutes);
        routesUnderBadge.add(bookRouteOne);

        Mockito.when(categoryService.getCategoryOfCurrentBadge(anyInt()))
                .thenReturn(currentCategory);
        Mockito.when(badgeService.getDateOfScoringLastBadgeWithGivenCategory(any(Category.class), anyInt()))
                .thenReturn(null);
        Mockito.when(bookService.getBookWithUserId(anyInt()))
                .thenReturn(book);
        Mockito.when(bookRouteDao.getBookRoutesByBookIdAndCategory(anyInt(), any(), any()))
                .thenReturn(routesUnderBadge);

        int result = bookRouteService.getPointsAwardedToCurrentBookRouteBasedOnHistory(sameRoute);

        assertEquals(0, result);

    }


    @Test
    public void getPointsAwardedToCurrentBookRouteBasedOnHistory_noSameRoutesCompletedBefore(){
        int pointsStartEnd = 2;
        int pointsEndStart = 5;

        var currentCategory = new Category(2, "Test_2");

        var book = new Book();
        book.setOwner(new Tourist());

        var route = new Route(pointsStartEnd, pointsEndStart, new Point(), new Point(), null);

        var sameRoute = new BookRoute(true,
                new Date(new java.util.Date().getTime()), currentCategory, route, book);


        Mockito.when(categoryService.getCategoryOfCurrentBadge(anyInt()))
                .thenReturn(currentCategory);
        Mockito.when(badgeService.getDateOfScoringLastBadgeWithGivenCategory(any(Category.class), anyInt()))
                .thenReturn(null);
        Mockito.when(bookService.getBookWithUserId(anyInt()))
                .thenReturn(book);
        Mockito.when(bookRouteDao.getBookRoutesByBookIdAndCategory(anyInt(), any(), any()))
                .thenReturn(newerBookRoutes);

        int result = bookRouteService.getPointsAwardedToCurrentBookRouteBasedOnHistory(sameRoute);

        assertEquals(pointsStartEnd, result);

    }


    @Test
    public void setPointsOfSameRoutesCompletedAfterGivenRoute_existingRouteAfterGiven() {
        int severalDays = 180000000;

        var routes = new ArrayList<BookRoute>();
        int pointsStartEnd = 2;
        int pointsEndStart = 5;
        var mutualRoute = new Route(pointsStartEnd, pointsEndStart, new Point(), new Point(), null);
        var book = new Book();
        book.setOwner(new Tourist());
        var category = new Category(2, "Test_2");

        var bookRouteLater = new BookRoute(true, new Date(new java.util.Date().getTime()),
                category, mutualRoute, book, pointsStartEnd);
        var bookRouteEarlier = new BookRoute(true, new Date(new java.util.Date().getTime()-severalDays),
                category, mutualRoute, book, pointsStartEnd);
        routes.add(bookRouteLater);

        bookRouteService.setPointsOfSameRoutesCompletedAfterGivenRoute(bookRouteEarlier, routes);

        assertEquals(pointsStartEnd, bookRouteEarlier.getPointsAwarded());
        assertEquals(0, bookRouteLater.getPointsAwarded());
    }

    @Test
    public void readRoutesCompletedUnderGivenBadge(){
        var allRoutes = new ArrayList<BookRoute>();
        allRoutes.addAll(oldBookRoutes);
        allRoutes.addAll(newerBookRoutes);


        var badges = new ArrayList<Badge>();
        badges.add(badgesScored.get(0));
        var firstBadge = badgesScored.get(0);

        Mockito.when(badgeService.getAllBadgesScoredByUser(anyInt()))
                .thenReturn(badges);
        Mockito.when(bookService.getBookWithUserId(anyInt()))
                .thenReturn(new Book());
        Mockito.when(bookRouteDao.getBookRoutesByBookId(anyInt()))
                .thenReturn(allRoutes);
        Mockito.when(badgeService.getPeriodOfScoringGivenBadge(any(), any()))
                .thenReturn(new java.util.Date[]{new Date(0), firstBadge.getAchievingDate()});
        var routesUnderFirstBadge = bookRouteService.readRoutesCompletedUnderGivenBadge(firstBadge, 1);

        assertEquals(oldBookRoutes, routesUnderFirstBadge);

    }

}
