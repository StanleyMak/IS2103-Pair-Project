/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRateEntity;
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

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    public Long createNewRentalRate(RentalRateEntity rentalRate) {
        em.persist(rentalRate);
        em.flush();
        
        return rentalRate.getRentalRateID();
    }
    
    public RentalRateEntity retrieveRentalRateByRentalRateID(Long rentalRateID) {
        RentalRateEntity rentalRate = em.find(RentalRateEntity.class, rentalRateID);
        return rentalRate;
    }
    
    public RentalRateEntity retrieveRentalRateByRentalRateName(String rentalRateName) {
        Query query = em.createQuery("SELECT r FROM RentalRateEntity r WHERE r.name = ?1")
                .setParameter(1, rentalRateName);
        RentalRateEntity rentalRate = (RentalRateEntity) query.getSingleResult();
        return rentalRate;
    }
    
    public void updateRentalRate(RentalRateEntity rentalRate) {
        
    }
    
    public void deleteRentalRate(Long rentalRateID) {
        RentalRateEntity rentalRate = em.find(RentalRateEntity.class, rentalRateID);
        
        //dissociate
        
        em.remove(rentalRate);
    }
}
