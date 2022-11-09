/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.RentalRateEntity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author stonley
 */
@Stateless
public class RentalRateSessionBean implements RentalRateSessionBeanRemote, RentalRateSessionBeanLocal {

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    
    @Override
    public Long createNewRentalRate(RentalRateEntity rentalRate, String categoryName) {
        CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
        rentalRate.setCarCategory(carCategory);
        em.persist(rentalRate);
        em.flush();
        
        return rentalRate.getRentalRateID();
    }
    
    @Override
    public List<RentalRateEntity> retrieveAllRentalRates() {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r ORDER BY r.carCategory.categoryName ASC, r.startDate ASC, r.endDate ASC");
        List<RentalRateEntity> rentalRates = query.getResultList();
        return rentalRates;
    }
    
    @Override
    public RentalRateEntity retrieveRentalRateByRentalRateID(Long rentalRateID) {
        RentalRateEntity rentalRate = em.find(RentalRateEntity.class, rentalRateID);
        //rentalRate.getXX().size();
        return rentalRate;
    }
    
    @Override
    public RentalRateEntity retrieveRentalRateByRentalRateName(String rentalRateName) {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r WHERE r.rentalName = ?1")
                .setParameter(1, rentalRateName);
        RentalRateEntity rentalRate = (RentalRateEntity) query.getSingleResult();
        return rentalRate;
    }
    
    @Override
    public void updateRentalRate(RentalRateEntity rentalRate, String categoryName) {
        CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
        rentalRate.setCarCategory(carCategory);
        em.merge(rentalRate);
        
    }
    
    @Override
    public void deleteRentalRate(Long rentalRateID) {
        RentalRateEntity rentalRate = retrieveRentalRateByRentalRateID(rentalRateID);
        
        //dissociate
        
        em.remove(rentalRate);
    }
}
