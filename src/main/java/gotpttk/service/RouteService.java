package gotpttk.service;

import gotpttk.entities.Route;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RouteService {

    @Autowired
    public EntityDao<Route, Integer> routeDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Route route){
        routeDao.saveOrUpdate(route);
    }

    public Route readById(int id){
        return routeDao.readById(id);
    }

    public List<Route> readAll(){
        return routeDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Route route){
        routeDao.delete(route);
    }
}

