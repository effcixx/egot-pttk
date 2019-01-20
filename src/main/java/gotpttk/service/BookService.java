package gotpttk.service;

import gotpttk.dao.BookDao;
import gotpttk.entities.Book;
import gotpttk.entities.BookRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    @Autowired
    public BookDao bookDao;

    @Autowired
    private BookRouteService bookRouteService;


    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Book book){
        bookDao.saveOrUpdate(book);
    }

    public Book readById(int id){
        return bookDao.readById(id);
    }

    public List<Book> readAll(){
        return bookDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Book book){
        bookDao.delete(book);
    }

    public void updatePointsAndBadgesAfterCompletionOfRoute(Book book){
        System.out.println("Validating...");
    }

    public Book getBookWithUserId(int userId){
        return bookDao.getBookWithUserId(userId);
    }

    public int getCurrentNumberOfPoints(int userId){
        var latestRoutes = bookRouteService.readRoutesUnderCurrentBadge(userId);
        System.out.println("******************************");
        latestRoutes.sort((b1, b2) -> b2.getDateOfCompletion().compareTo(b1.getDateOfCompletion()));
        for (var r : latestRoutes){
            System.out.println(r.getPointsAwarded() + ": " + r.getRoute());
        }
        System.out.println("******************************");
        return latestRoutes.stream().mapToInt(BookRoute::getPointsAwarded).sum();
    }


}

