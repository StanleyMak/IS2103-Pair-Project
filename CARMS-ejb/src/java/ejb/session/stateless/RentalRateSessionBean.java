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
import util.exception.CarCategoryNotFoundException;
import util.exception.DeleteRentalRateException;

/**
 *
 * @author stonley
 */
@Stateless
public class RentalRateSessionBean implements RentalRateSessionBeanRemote, RentalRateSessionBeanLocal {

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    
    @Override
    public Long createNewRentalRate(RentalRateEntity rentalRate, String categoryName) throws CarCategoryNotFoundException {
        try {
            CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
            rentalRate.setCarCategory(carCategory);
            em.persist(rentalRate);
            em.flush();
            
            return rentalRate.getRentalRateID();
        } catch (CarCategoryNotFoundException e) {
            throw new CarCategoryNotFoundException("Car Category Name " + categoryName + "does not exist!\n");
        }
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
    
    public List<RentalRateEntity> retrieveRentalRatesOfCarCategory(String carCategoryName) {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r WHERE r.carCategory.categoryName = ?1")
                .setParameter(1, carCategoryName);
        List<RentalRateEntity> rentalRates = query.getResultList();
        return rentalRates;
    }
    
    @Override
    public void updateRentalRate(RentalRateEntity rentalRate, String categoryName) throws CarCategoryNotFoundException {
        try {
            CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(categoryName);
            rentalRate.setCarCategory(carCategory);
            em.merge(rentalRate);
        } catch (CarCategoryNotFoundException e) {
            throw new CarCategoryNotFoundException("Car Category Name " + categoryName + "does not exist!\n");
        }
        
    }
    
    @Override
    public void deleteRentalRate(Long rentalRateID) throws DeleteRentalRateException {
        RentalRateEntity rentalRate = retrieveRentalRateByRentalRateID(rentalRateID);
        
        //dissociate
        
        em.remove(rentalRate);
        
       

        if (reservationSessionBeanLocal.retrieveReservationsOfRentalRateID(rentalRateID).isEmpty()) {
            em.remove(rentalRate);
        } else {
            rentalRate.setIsDisabled(Boolean.TRUE);
            throw new DeleteRentalRateException("Rental Rate ID " + rentalRateID + " is associated with existing reservation(s) and cannot be deleted!");
        }
    }
}
