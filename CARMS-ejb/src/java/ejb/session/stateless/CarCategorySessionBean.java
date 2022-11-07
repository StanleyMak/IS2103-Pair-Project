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

    @Override
    public Long createNewCarCategory(CarCategoryEntity carCategory) {
        em.persist(carCategory);
        em.flush();
        return carCategory.getCarCategoryID();
    }

    @Override
    public CarCategoryEntity retrieveCarCategoryByCarCategoryID(Long carCategoryID) {
        CarCategoryEntity carCategory = em.find(CarCategoryEntity.class, carCategoryID);
        //carCategory.getXXX().size();
        return carCategory;
    }

    @Override
    public CarCategoryEntity retrieveCarCategoryByCarCategoryName(String carCategoryName) {
        Query query = em.createQuery("SELECT c FROM CarCategoryEntity c WHERE c.name = ?1")
                .setParameter(1, carCategoryName);

        CarCategoryEntity carCategory = (CarCategoryEntity) query.getSingleResult();

        return carCategory;
    }
    
    @Override
     public void deleteCarCategory(Long carCategoryID) {
         CarCategoryEntity carCategory = retrieveCarCategoryByCarCategoryID(carCategoryID);
         //dissociate
         em.remove(carCategory);
     }

}
