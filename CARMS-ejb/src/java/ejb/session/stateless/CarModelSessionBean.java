/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CarCategoryEntity;
import entity.CarModelEntity;
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
public class CarModelSessionBean implements CarModelSessionBeanRemote, CarModelSessionBeanLocal {

    @EJB(name = "CarCategorySessionBeanLocal")
    private CarCategorySessionBeanLocal carCategorySessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    

    @Override
    public Long createNewCarModel(CarModelEntity carModel, String carCategoryName) {
        CarCategoryEntity carCategory = carCategorySessionBeanLocal.retrieveCarCategoryByCarCategoryName(carCategoryName);
        carCategory.getCarModels().add(carModel); //shld it be uni where model points at category
        carModel.setCategory(carCategory);
        em.persist(carModel);
        em.flush();
        
        return carModel.getCarModelID();
    }
    
    @Override
    public CarModelEntity retrieveCarModelByCarModelID(Long carModelID) {
        CarModelEntity carModel = em.find(CarModelEntity.class, carModelID);
        //carModel.getXX().size();
        return carModel;
    }
    
    @Override
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
