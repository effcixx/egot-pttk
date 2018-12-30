package gotpttk.service;

import gotpttk.entities.Category;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    public EntityDao<Category, Integer> categoryDao;

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
}
