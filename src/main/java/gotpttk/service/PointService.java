package gotpttk.service;

import gotpttk.entities.Point;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PointService {

    @Autowired
    public EntityDao<Point, Integer> pointDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Point point){
        pointDao.saveOrUpdate(point);
    }

    public Point readById(int id){
        return pointDao.readById(id);
    }

    public List<Point> readAll(){
        return pointDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Point point){
        pointDao.delete(point);
    }
}

