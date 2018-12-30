package gotpttk.service;

import gotpttk.entities.Range;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RangeService {

    @Autowired
    public EntityDao<Range, Integer> rangeDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Range range){
        rangeDao.saveOrUpdate(range);
    }

    public Range readById(int id){
        return rangeDao.readById(id);
    }

    public List<Range> readAll(){
        return rangeDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Range range){
        rangeDao.delete(range);
    }
}
