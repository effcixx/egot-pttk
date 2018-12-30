package gotpttk.service;

import gotpttk.entities.Admin;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    @Autowired
    public EntityDao<Admin, Integer> adminDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Admin admin){
        adminDao.saveOrUpdate(admin);
    }

    public Admin readById(int id){
        return adminDao.readById(id);
    }

    public List<Admin> readAll(){
        return adminDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Admin admin){
        adminDao.delete(admin);
    }
}
