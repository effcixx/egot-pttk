package gotpttk.service;

import gotpttk.entities.Region;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegionService {

    @Autowired
    public EntityDao<Region, Integer> regionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Region region){
        regionDao.saveOrUpdate(region);
    }

    public Region readById(int id){
        return regionDao.readById(id);
    }

    public List<Region> readAll(){
        return regionDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Region region){
        regionDao.delete(region);
    }
}
