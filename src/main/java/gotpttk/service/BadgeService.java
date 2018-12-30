package gotpttk.service;

import gotpttk.entities.Badge;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BadgeService {

    @Autowired
    public EntityDao<Badge, Integer> badgeDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Badge badge){
        badgeDao.saveOrUpdate(badge);
    }

    public Badge readById(int id){
        return badgeDao.readById(id);
    }

    public List<Badge> readAll(){
        return badgeDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Badge badge){
        badgeDao.delete(badge);
    }
}
