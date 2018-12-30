package gotpttk.service;

import gotpttk.entities.Book;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    @Autowired
    public EntityDao<Book, Integer> bookDao;

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
}

