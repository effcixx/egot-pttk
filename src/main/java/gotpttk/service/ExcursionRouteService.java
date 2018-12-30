package gotpttk.service;

import gotpttk.entities.ExcursionRoute;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExcursionRouteService {

    @Autowired
    public EntityDao<ExcursionRoute, Integer> excursionRouteDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(ExcursionRoute excursionRoute){
        excursionRouteDao.saveOrUpdate(excursionRoute);
    }

    public ExcursionRoute readById(int id){
        return excursionRouteDao.readById(id);
    }

    public List<ExcursionRoute> readAll(){
        return excursionRouteDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(ExcursionRoute excursionRoute){
        excursionRouteDao.delete(excursionRoute);
    }
}
