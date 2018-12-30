package gotpttk.service;

import gotpttk.entities.Excursion;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExcursionService {

    @Autowired
    public EntityDao<Excursion, Integer> excursionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Excursion excursion){
        excursionDao.saveOrUpdate(excursion);
    }

    public Excursion readById(int id){
        return excursionDao.readById(id);
    }

    public List<Excursion> readAll(){
        return excursionDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Excursion excursion){
        excursionDao.delete(excursion);
    }
}
