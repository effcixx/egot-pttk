package gotpttk.service;

import gotpttk.entities.BookRoute;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookRouteService {

    @Autowired
    public EntityDao<BookRoute, Integer> bookRouteDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(BookRoute bookRoute){
        bookRouteDao.saveOrUpdate(bookRoute);
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
}

