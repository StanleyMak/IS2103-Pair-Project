/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarModelEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author stonley
 */
@Stateless
public class CarModelSessionBean implements CarModelSessionBeanRemote, CarModelSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    public Long createNewCarModel(CarModelEntity carModel) {
        em.persist(carModel);
        em.flush();
        
        return carModel.getCarModelID();
    }
    
    public CarModelEntity retrieveCarModelByCarModelID(Long carModelID) {
        CarModelEntity carModel = em.find(CarModelEntity.class, carModelID);
        //carModel.getXX().size();
        return carModel;
    }
    
    public CarModelEntity retrieveCarModelByCarModel(String carModelModel) {
        Query query = em.createQuery("SELECT c FROM CarModel c WHERE c.model = ?1")
                .setParameter(1, carModelModel);
        CarModelEntity carModel = (CarModelEntity) query.getSingleResult();
        //carModel.getXX().size();
        return carModel;
    }
    
    private void updateCarModel(CarModelEntity carModel) {
        
    }
    
    private void deleteCarModel(Long carModelID) {
        CarModelEntity carModel = em.find(CarModelEntity.class, carModelID);
        //dissociate
        
        em.remove(carModel);
    }

    
}
