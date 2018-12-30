package gotpttk.service;

import gotpttk.entities.Tourist;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TouristService {

    @Autowired
    public EntityDao<Tourist, Integer> touristDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Tourist tourist){
        touristDao.saveOrUpdate(tourist);
    }

    public Tourist readById(int id){
        return touristDao.readById(id);
    }

    public List<Tourist> readAll(){
        return touristDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Tourist tourist){
        touristDao.delete(tourist);
    }
}

