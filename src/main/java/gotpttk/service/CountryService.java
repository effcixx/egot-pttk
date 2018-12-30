package gotpttk.service;

import gotpttk.entities.Country;
import gotpttk.dao.EntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CountryService {

    @Autowired
    public EntityDao<Country, Integer> countryDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Country country){
        countryDao.saveOrUpdate(country);
    }

    public Country readById(int id){
        return countryDao.readById(id);
    }

    public List<Country> readAll(){
        return countryDao.readAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Country country){
        countryDao.delete(country);
    }
}
