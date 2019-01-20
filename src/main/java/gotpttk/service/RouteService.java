package gotpttk.service;

import gotpttk.dao.RouteDao;
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
    public RouteDao routeDao;

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

    /// TODO ZMIENIC

    public List<Route> readAllPublic(){
        return routeDao.readAllPublic();
    }

    public List<Route> readRoutesDefinedByUser(int userId) { return routeDao.readRoutesDefinedByUser(userId); }

}

