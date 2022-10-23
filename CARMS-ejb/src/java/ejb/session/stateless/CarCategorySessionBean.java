/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarModelEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hanyang
 */
@Stateless
public class CarCategorySessionBean implements CarCategorySessionBeanRemote, CarCategorySessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    public Long createNewCarCategory(CarCategoryEntity carCategory) {
        em.persist(carCategory);
        em.flush();
        return carCategory.getCarCategoryID();
}
    
    public CarCategoryEntity retrieveCarCategory(Long carCategoryID) {
        return em.find(CarCategoryEntity.class, carCategoryID);
    }
    
    public CarCategoryEntity retrieveCarCategoryByCarCategory(String carCategory) {
        Query query = em.createQuery("SELECT c FROM CarCategoryEntity c WHERE c.category = ?1")
                .setParameter(1, carCategory);
        
        return (CarCategoryEntity) query.getSingleResult();
    }
    //
    
}
